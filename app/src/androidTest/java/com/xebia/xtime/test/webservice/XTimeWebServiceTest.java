package com.xebia.xtime.test.webservice;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.xebia.xtime.shared.model.Project;
import com.xebia.xtime.shared.model.TimeCell;
import com.xebia.xtime.shared.model.TimeSheetEntry;
import com.xebia.xtime.shared.model.WorkType;
import com.xebia.xtime.test.Mocks;
import com.xebia.xtime.webservice.XTimeWebService;

import java.util.Date;

import okhttp3.Cookie;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class XTimeWebServiceTest extends InstrumentationTestCase {

    private MockWebServer mServer;
    private XTimeWebService mWebService;
    private Context mContext;

    public void setUp() throws Exception {
        super.setUp();
        mServer = new MockWebServer();
        mServer.start();
        XTimeWebService.init(new MockCookieJar());
        mWebService = XTimeWebService.getInstance();
        mWebService.setBaseUrl(mServer.url("/xtime/").toString());
        mContext = getInstrumentation().getContext();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        mServer.shutdown();
    }

    public void testLogin() throws Exception {
        final Cookie cookie = Cookie.parse(mServer.url("/xtime/entryform.html"),
                "JSESSIONID=BEEF38177C0CA25A303951D5944A7A5B; Path=/xtime/; Secure; HttpOnly");
        mServer.enqueue(new MockResponse()
                .setResponseCode(302)
                .setHeader("Set-Cookie", cookie)
                .setHeader("Location", mServer.url("/xtime/entryform.html").toString()));
        mServer.enqueue(new MockResponse()
                .setResponseCode(200));

        Cookie result = mWebService.login("foo", "bar");
        RecordedRequest request = mServer.takeRequest();

        assertEquals(cookie.toString(), result.toString());
        assertEquals("j_username=foo&j_password=bar", request.getBody().readUtf8());
        assertEquals("application/x-www-form-urlencoded; charset=utf-8",
                request.getHeader("Content-Type"));
    }

    public void testGetMonthOverview() throws Exception {
        final String mockResponse = Mocks.getMonthOverviewResponse(mContext);
        mServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse));

        String result = mWebService.getMonthOverview(new Date(1388588275000l));
        RecordedRequest request = mServer.takeRequest();

        assertEquals(mockResponse, result);
        assertEquals("callCount=1\n"
                + "page=/xtime/monthlyApprove.html\n"
                + "httpSessionId=\n"
                + "scriptSessionId=\n"
                + "c0-scriptName=TimeEntryServiceBean\n"
                + "c0-methodName=getMonthOverview\n"
                + "c0-id=0\n"
                + "c0-param0=string:2014-01-01\n"
                + "batchId=0", request.getBody().readUtf8());
        assertEquals("text/plain; charset=utf-8", request.getHeader("Content-Type"));
        assertEquals(MockCookieJar.COOKIE_VALUE, request.getHeader("Cookie"));
    }

    public void testGetWeekOverview() throws Exception {
        final String mockResponse = Mocks.getWeekOverviewResponse(mContext);
        mServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse));

        String result = mWebService.getWeekOverview(new Date(1388588275000l));
        RecordedRequest request = mServer.takeRequest();

        assertEquals(mockResponse, result);
        assertEquals("callCount=1\n"
                + "page=/xtime/entryform.html\n"
                + "httpSessionId=\n"
                + "scriptSessionId=\n"
                + "c0-scriptName=TimeEntryServiceBean\n"
                + "c0-methodName=getWeekOverview\n"
                + "c0-id=0\n"
                + "c0-param0=string:2014-01-01\n"
                + "c0-param1=boolean:true\n"
                + "batchId=0\n", request.getBody().readUtf8());
        assertEquals("text/plain; charset=utf-8", request.getHeader("Content-Type"));
        assertEquals(MockCookieJar.COOKIE_VALUE, request.getHeader("Cookie"));
    }

    public void testGetWorkTypesForProject() throws Exception {
        final String mockResponse = Mocks.getWorkTypesForProjectResponse(mContext);
        mServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse));
        Project project = new Project("31415", "some project name");
        Date week = new Date(1426287600000l); // Sat, 14 Mar 2015, 0:00:00 CET
        String result = mWebService.getWorkTypesForProject(project, week);
        RecordedRequest request = mServer.takeRequest();

        assertEquals(mockResponse, result);
        assertEquals("callCount=1\n"
                + "page=/xtime/entryform.html\n"
                + "httpSessionId=\n"
                + "scriptSessionId=\n"
                + "c0-scriptName=TimeEntryServiceBean\n"
                + "c0-methodName=getWorkTypesListForProjectInRange\n"
                + "c0-id=0\n"
                + "c0-param0=string:31415\n"
                + "c0-param1=string:2015-03-14\n"
                + "c0-param2=boolean:true\n"
                + "batchId=0", request.getBody().readUtf8());
        assertEquals("text/plain; charset=utf-8", request.getHeader("Content-Type"));
        assertEquals(MockCookieJar.COOKIE_VALUE, request.getHeader("Cookie"));
    }

    public void testApproveMonth() throws Exception {
        mServer.enqueue(new MockResponse()
                .setResponseCode(302)
                .setHeader("Location", mServer.url("/xtime/monthlyApproveConfirmation.html")
                        .toString()));
        mServer.enqueue(new MockResponse()
                .setResponseCode(200));

        boolean result = mWebService.approveMonth(67, new Date(1388588275000l));
        RecordedRequest request = mServer.takeRequest();

        assertTrue(result);
        assertEquals("approvalMonthYear=2014-01-01&approve=Approve&inp_grandTotal=67",
                request.getBody().readUtf8());
        assertEquals("application/x-www-form-urlencoded; charset=utf-8",
                request.getHeader("Content-Type"));
        assertEquals(MockCookieJar.COOKIE_VALUE, request.getHeader("Cookie"));
    }

    public void testDeleteTimeEntry() throws Exception {
        final String mockResponse = Mocks.deleteTimeEntryResponse(mContext);
        mServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse));
        Date date = new Date(1426287600000l); // Sat, 14 Mar 2015, 0:00:00 CET
        TimeSheetEntry timeEntry = new TimeSheetEntry(new Project("1", "foo"),
                new WorkType("940", "bar"), "description", new TimeCell(date, 8, false));
        String result = mWebService.deleteEntry(timeEntry);
        RecordedRequest request = mServer.takeRequest();

        assertEquals(mockResponse, result);
        assertEquals("callCount=1\n"
                + "page=/xtime/entryform.html\n"
                + "httpSessionId=\n"
                + "scriptSessionId=\n"
                + "c0-scriptName=TimeEntryServiceBean\n"
                + "c0-methodName=deleteTimeSheetEntries\n"
                + "c0-id=0\n"
                + "c0-param0=string:1\n"
                + "c0-param1=string:940\n"
                + "c0-param2=string:description\n"
                + "c0-param3=string:2015-03-14\n"
                + "batchId=0", request.getBody().readUtf8());
        assertEquals("text/plain; charset=utf-8", request.getHeader("Content-Type"));
        assertEquals(MockCookieJar.COOKIE_VALUE, request.getHeader("Cookie"));
    }

    public void testSaveEntry() throws Exception {
        mServer.enqueue(new MockResponse()
                .setResponseCode(302)
                .setHeader("Location", mServer.url("/xtime/entryform.html").toString()));
        mServer.enqueue(new MockResponse().setResponseCode(200));
        // request to write 3.14 hours on some project on pi day
        Project project = new Project("42", "some project name");
        WorkType workType = new WorkType("100", "some work description");
        String description = "foo bar";
        Date date = new Date(1426287600000l); // Sat, 14 Mar 2015, 0:00:00 CET
        TimeCell timeCell = new TimeCell(date, 3.14, false);
        TimeSheetEntry timeEntry = new TimeSheetEntry(project, workType, description, timeCell);

        Boolean result = mWebService.saveEntry(timeEntry);
        RecordedRequest request = mServer.takeRequest();

        assertTrue(result);
        assertEquals("startDate=9+Mar+2015&endDate=15+Mar+2015" +
                "&weekDates=2015-03-09&weekDates=2015-03-10&weekDates=2015-03-11" +
                "&weekDates=2015-03-12&weekDates=2015-03-13&weekDates=2015-03-14" +
                "&weekDates=2015-03-15" +
                "&projectId=42&workType=100&description=foo+bar" +
                "&monday=&tuesday=&wednesday=&thursday=&friday=&saturday=3.14&sunday=" +
                "&weekTotal1=3.14&projectId=&workType=&description=" +
                "&monday=&tuesday=&wednesday=&thursday=&friday=&saturday=&sunday=" +
                "&weekTotal1=&projectId=&workType=&description=" +
                "&monday=&tuesday=&wednesday=&thursday=&friday=&saturday=&sunday=" +
                "&weekTotal1=&projectId=&workType=&description=" +
                "&monday=&tuesday=&wednesday=&thursday=&friday=&saturday=&sunday=" +
                "&weekTotal1=&projectId=&workType=&description=" +
                "&monday=&tuesday=&wednesday=&thursday=&friday=&saturday=&sunday=" +
                "&weekTotal1=&dayTotal1=&dayTotal2=&dayTotal3=" +
                "&dayTotal4=&dayTotal5=&dayTotal6=3.14&dayTotal7=" +
                "&grandTotal=3.14&buttonClicked=save",
                request.getBody().readUtf8());
        assertEquals("application/x-www-form-urlencoded; charset=utf-8",
                request.getHeader("Content-Type"));
    }
}
