package org.Heron.models;


import lombok.*;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class MassSpecData {


    public String peptide;

    private String protein;

    private String replicateName;

    private float ratioToStandard;

    private double pepRetentionTime;

    private float quantification;

    private String quantificationWithConcentration;

    private double pepPeakFoundRatio;



}
