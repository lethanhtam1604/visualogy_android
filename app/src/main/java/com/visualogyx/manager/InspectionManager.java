package com.visualogyx.manager;

import com.visualogyx.model.Inspection;

import java.util.ArrayList;

public class InspectionManager {

    private static InspectionManager instance = new InspectionManager();

    public static InspectionManager getInstance() {
        return instance;
    }

    public ArrayList<Inspection> inspections;

    private InspectionManager() {
        inspections = new ArrayList<>();

//        inspections.add(createInspection("East School", true));

    }

    private Inspection createInspection(String name, boolean isConnected) {
        Inspection inspection = new Inspection();
//        inspection.setName(name);
//        inspection.setConnected(isConnected);
        return inspection;
    }
}
