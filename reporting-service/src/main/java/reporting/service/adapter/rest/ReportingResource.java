package reporting.service.adapter.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import reporting.service.LoggedTransaction;
import reporting.service.ReportingService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Path("/reports")
public class ReportingResource {
	ReportingService service = new ReportingFactory().getService();

	//GET Reports as customer
	@GET
	@Path("/customer/{customerId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LoggedTransaction> getReportsCustomer(@PathParam("customerId") String customerId) {
		return service.getReportsCustomer(customerId);
	}

	//GET Reports as merchant
	@GET
	@Path("/merchant/{merchantId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LoggedTransaction> getReportsMerchant(@PathParam("merchantId") String merchantId) {
		return new ArrayList<>(){{add(new LoggedTransaction(BigDecimal.valueOf(1000), "Merchant", "", ""));}};
//		return service.getReportsMerchant(merchantId);
	}

	//GET Reports as manager
	@GET
	@Path("/manager")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LoggedTransaction> getReportsManager() {
		return new ArrayList<>(){{add(new LoggedTransaction(BigDecimal.valueOf(1000), "Manager", "", ""));}};
//		return service.getReportsManager();
	}
}
