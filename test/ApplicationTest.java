import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;
import java.util.*;
public class ApplicationTest extends FunctionalTest {

    @Test
    public void testThatIndexPageWorks() {
//        Response response = GET("/");
//        assertIsOk(response);
//        assertContentType("text/html", response);
//        assertCharset(play.Play.defaultWebEncoding, response);
//    	List<CaliforniaCandidates> c = CaliforniaCandidates.find("select c from  CaliforniaCandidates c").fetch();
//		System.out.println("this is what c is ["+c+"]");
		
    	List<CaliforniaCommittees> c = CaliforniaCommittees.find("select c from  CaliforniaCommittees c").fetch();
		System.out.println("this is what california committees are ["+c+"]");
		
//    	System.out.println(VWCandidates.getSessionDropDown());
    			 //   JPAQry query  = CaliforniaCandidates.find("SELECT c FROM CaliforniaCandidates c , in (c.incumbents)  ci " +
    				//											  "where ci.description = ?" ,"112th Congress");
    			//	CaliforniaCandidates c = (CandidateContributions) query.fetch().get(0);
    						
    			//	List <VWCandidates> c = VWCandidates.all().fetch(1);
    				
    			//	List<VWCandidates> x = c.get(0).cand;
    				
//    				System.out.println(c.get(0).cand.toString());
    									//// select c from  CandidateContributions c, in (c.cand) as a where a.description = ? 
    				
    			//	List<String> c = CandidateContributions.getOfficeNames();
    				
    			//	System.out.println(c.toString());
    				
    				//List<VWIncumbentsController> c = VWIncumbentsController.all().fetch(1);
    				//System.out.println(VWIncumbents.getCongresses());
    				
    				//play.modules.search.Query q = Search.search("DonorName:Chevron", CandidateContributions.class);
    				//System.out.println(q.all().fetch().toString());
    				

    }
    
}