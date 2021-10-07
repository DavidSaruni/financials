package org.openmrs.module.financials.fragment.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.financials.GeneralRevenuePerUnit;
import org.openmrs.module.hospitalcore.HospitalCoreService;
import org.openmrs.module.hospitalcore.model.OpdTestOrder;
import org.openmrs.module.hospitalcore.model.PatientServiceBillItem;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;

public class DepartmentFinanceSummariesFragmentController {
	
	public void controller(FragmentModel model) {
		List<OpdTestOrder> allOpdOrders = Context.getService(HospitalCoreService.class).getAllOpdOrdersByDateRange(false,
		    "", "");
		List<PatientServiceBillItem> patientServiceBillItems = Context.getService(HospitalCoreService.class)
		        .getAllPatientServiceBillItemsByDate(false, "", "");
		GeneralRevenuePerUnit generalRevenuePerUnit = null;
		List<GeneralRevenuePerUnit> summarizedResults = new ArrayList<GeneralRevenuePerUnit>();
		
		for (OpdTestOrder opdTestOrder : allOpdOrders) {
			for (PatientServiceBillItem patientServiceBillItem : patientServiceBillItems) {
				if (opdTestOrder.getBillableService().equals(patientServiceBillItem.getService())
				        && opdTestOrder.getFromDept() != null) {
					generalRevenuePerUnit = new GeneralRevenuePerUnit();
					generalRevenuePerUnit.setTransactionDate(opdTestOrder.getScheduleDate());
					
					if (opdTestOrder.getConcept().equals(
					    Context.getConceptService().getConceptByUuid("0179f241-8c1d-47c1-8128-841f6508e251"))) {
						generalRevenuePerUnit.setDepartment("LABORATORY");
						generalRevenuePerUnit.setServicePaidFor(opdTestOrder.getConcept().getName().getName());
					} else if (opdTestOrder.getConcept().equals(
					    Context.getConceptService().getConceptByUuid("1651AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))) {
						generalRevenuePerUnit.setDepartment("PROCEDURE ROOM");
						generalRevenuePerUnit.setServicePaidFor(opdTestOrder.getConcept().getName().getName());
					} else {
						generalRevenuePerUnit.setDepartment(opdTestOrder.getFromDept());
						generalRevenuePerUnit.setServicePaidFor(opdTestOrder.getConcept().getName().getName());
					}
					generalRevenuePerUnit.setTotalAmount(patientServiceBillItem.getActualAmount());
					break;
				}
			}
			summarizedResults.add(generalRevenuePerUnit);
		}
		
		model.addAttribute("summaryAccounts", summarizedResults);
	}
}
