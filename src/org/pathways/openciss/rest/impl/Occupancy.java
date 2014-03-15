package org.pathways.openciss.rest.impl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.pathways.openciss.model.ProgramProfileInfo;

import org.pathways.openciss.shared.ProgramProfileInfoService;

@Path("/bed_units")
public class Occupancy {
	ProgramProfileInfoService ppis = new ProgramProfileInfoService();

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/occupied")
	public String getOccupied(@QueryParam("id") String id) {
		ProgramProfileInfo ppi = ppis.getProgramProfileInfo(Integer.valueOf(id));
		String result = String.valueOf(ppi.getUnitsOccupied());
		return result;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/occupied/set")
	public String setOccupied(@QueryParam("id") String id, @QueryParam("val") String val) {
		String result = null;
		if (id != null) {
			ProgramProfileInfo ppiToPersist = new ProgramProfileInfo();
			ppiToPersist.setAgencyKey(Integer.valueOf(id));
			ProgramProfileInfo ppiPersisted = ppis.getProgramProfileInfo(Integer.valueOf(id));
			int occupiedUnits = ppiPersisted.getUnitsOccupied();
			occupiedUnits = Integer.parseInt(val);
			ppiToPersist.setUnitsOccupied(occupiedUnits);
			// also recalculate available units
			int availUnits = ppiPersisted.getUnitsAvailable();
			int totalUnits = ppiPersisted.getUnitsTotal();
			availUnits = totalUnits - occupiedUnits;
			ppiToPersist.setUnitsAvailable(availUnits);
			ppiToPersist.setUnitsTotal(totalUnits);
			ppis.updateProgramProfileInfo_Occupany(ppiToPersist);
			result = String.valueOf(occupiedUnits);
		}
		return result;
	}	
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/occupied/inc")
	public String incOccupied(@QueryParam("id") String id) {
		String result = null;
		if (id != null) {
			ProgramProfileInfo ppiToPersist = new ProgramProfileInfo();
			ppiToPersist.setAgencyKey(Integer.valueOf(id));
			ProgramProfileInfo ppiPersisted = ppis.getProgramProfileInfo(Integer.valueOf(id));
			int occUnits = ppiPersisted.getUnitsOccupied();
			occUnits = occUnits + 1;
			ppiToPersist.setUnitsOccupied(occUnits);
			int totalUnits = ppiPersisted.getUnitsTotal();
			ppiToPersist.setUnitsTotal(totalUnits);
			ppiToPersist.setUnitsAvailable(totalUnits - occUnits);
			ppis.updateProgramProfileInfo_Occupany(ppiToPersist);
			result = String.valueOf(occUnits);
		}
		return result;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/occupied/dec")
	public String decOccupied(@QueryParam("id") String id) {
		String result = null;
		if (id != null) {
			ProgramProfileInfo ppiToPersist = new ProgramProfileInfo();
			ppiToPersist.setAgencyKey(Integer.valueOf(id));
			ProgramProfileInfo ppiPersisted = ppis.getProgramProfileInfo(Integer.valueOf(id));
			int occUnits = ppiPersisted.getUnitsOccupied();
			occUnits = occUnits - 1;
			ppiToPersist.setUnitsOccupied(occUnits);
			int totalUnits = ppiPersisted.getUnitsTotal();
			ppiToPersist.setUnitsTotal(totalUnits);
			ppiToPersist.setUnitsAvailable(totalUnits - occUnits);
			ppis.updateProgramProfileInfo_Occupany(ppiToPersist);
			result = String.valueOf(occUnits);
		}
		return result;
	}	
	
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/available")
	public String getAvailable(@QueryParam("id") String id) {
		ProgramProfileInfo ppi = ppis.getProgramProfileInfo(Integer.valueOf(id));
		String result = String.valueOf(ppi.getUnitsAvailable());
		return result;
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/total")
	public String getTotal(@QueryParam("id") String id) {
		ProgramProfileInfo ppi = ppis.getProgramProfileInfo(Integer.parseInt(id));
		String result = String.valueOf(ppi.getUnitsTotal());
		return result;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/total/set")
	public String setTotal(@QueryParam("id") String id, @QueryParam("val") String val) {
		String result = null;
		if (id != null) {
			ProgramProfileInfo ppiToPersist = new ProgramProfileInfo();
			ppiToPersist.setAgencyKey(Integer.valueOf(id));
			ProgramProfileInfo ppiPersisted = ppis.getProgramProfileInfo(Integer.valueOf(id));
			int totalUnits = ppiPersisted.getUnitsTotal();
			totalUnits = Integer.parseInt(val);
			ppiToPersist.setUnitsTotal(totalUnits);
			// also recalculate available units
			int availUnits = ppiPersisted.getUnitsAvailable();
			int occUnits = ppiPersisted.getUnitsOccupied();
			availUnits = totalUnits - occUnits;
			ppiToPersist.setUnitsAvailable(availUnits);
			ppiToPersist.setUnitsOccupied(occUnits);
			ppis.updateProgramProfileInfo_Occupany(ppiToPersist);
			result = String.valueOf(totalUnits);
		}
		return result;
	}	
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/total/inc")
	public String incTotal(@QueryParam("id") String id) {
		String result = null;
		if (id != null) {
			ProgramProfileInfo ppiToPersist = new ProgramProfileInfo();
			ppiToPersist.setAgencyKey(Integer.valueOf(id));
			ProgramProfileInfo ppiPersisted = ppis.getProgramProfileInfo(Integer.valueOf(id));
			int totalUnits = ppiPersisted.getUnitsTotal();
			totalUnits = totalUnits + 1;
			ppiToPersist.setUnitsTotal(totalUnits);
			// also recalculate available units
			int availUnits = ppiPersisted.getUnitsAvailable();
			int occUnits = ppiPersisted.getUnitsOccupied();
			availUnits = totalUnits - occUnits;
			ppiToPersist.setUnitsAvailable(availUnits);
			ppiToPersist.setUnitsOccupied(occUnits);
			ppis.updateProgramProfileInfo_Occupany(ppiToPersist);
			result = String.valueOf(totalUnits);
		}
		return result;
	}	
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/total/dec")
	public String decTotal(@QueryParam("id") String id) {
		String result = null;
		if (id != null) {
			ProgramProfileInfo ppiToPersist = new ProgramProfileInfo();
			ppiToPersist.setAgencyKey(Integer.valueOf(id));
			ProgramProfileInfo ppiPersisted = ppis.getProgramProfileInfo(Integer.valueOf(id));
			int totalUnits = ppiPersisted.getUnitsTotal();
			totalUnits = totalUnits - 1;
			ppiToPersist.setUnitsTotal(totalUnits);
			// also recalculate available units
			int availUnits = ppiPersisted.getUnitsAvailable();
			int occUnits = ppiPersisted.getUnitsOccupied();
			availUnits = totalUnits - occUnits;
			ppiToPersist.setUnitsAvailable(availUnits);
			ppiToPersist.setUnitsOccupied(occUnits);
			ppis.updateProgramProfileInfo_Occupany(ppiToPersist);
			result = String.valueOf(totalUnits);
		}
		return result;
	}	
}
