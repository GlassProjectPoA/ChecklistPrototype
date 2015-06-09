package com.medialabamsterdam.checklistprototype.ContainerClasses;

import java.util.Arrays;

/**
 * This class represents a Detail with all it's possible parameters.
 * <p>
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 11/05/2015.
 */
public class Detail {

    private int id;
    private String[] description;
    private String[] description_nl;
    private String details;
    private String details_nl;
    private String title_1;
    private String title_1_nl;
    private String[] rating_1;
    private String[] rating_1_nl;
    private String title_2;
    private String title_2_nl;
    private String[] rating_2;
    private String[] rating_2_nl;
    private String title_3;
    private String title_3_nl;
    private String[] rating_3;
    private String[] rating_3_nl;

    public Detail(int id, String[] description, String[] description_nl, String details,
                  String details_nl, String title_1, String title_1_nl, String[] rating_1,
                  String[] rating_1_nl, String title_2, String title_2_nl, String[] rating_2,
                  String[] rating_2_nl, String title_3, String title_3_nl, String[] rating_3,
                  String[] rating_3_nl) {

        this.id = id;
        this.description = description;
        this.description_nl = description_nl;
        this.details = details;
        this.details_nl = details_nl;
        this.title_1 = title_1;
        this.title_1_nl = title_1_nl;
        this.rating_1 = rating_1;
        this.rating_1_nl = rating_1_nl;
        this.title_2 = title_2;
        this.title_2_nl = title_2_nl;
        this.rating_2 = rating_2;
        this.rating_2_nl = rating_2_nl;
        this.title_3 = title_3;
        this.title_3_nl = title_3_nl;
        this.rating_3 = rating_3;
        this.rating_3_nl = rating_3_nl;
    }

    public Detail() {
    }

    public int getId() {
        return id;
    }

    public String[] getDescription() {
        return description;
    }

    public String[] getDescription_nl() {
        return description_nl;
    }

    public String getDetails() {
        return details;
    }

    public String getDetails_nl() {
        return details_nl;
    }

    public String getTitle_1() {
        return title_1;
    }

    public String getTitle_1_nl() {
        return title_1_nl;
    }

    public String[] getRating_1() {
        return rating_1;
    }

    public String[] getRating_1_nl() {
        return rating_1_nl;
    }

    public String getTitle_2() {
        return title_2;
    }

    public String getTitle_2_nl() {
        return title_2_nl;
    }

    public String[] getRating_2() {
        return rating_2;
    }

    public String[] getRating_2_nl() {
        return rating_2_nl;
    }

    public String getTitle_3() {
        return title_3;
    }

    public String getTitle_3_nl() {
        return title_3_nl;
    }

    public String[] getRating_3() {
        return rating_3;
    }

    public String[] getRating_3_nl() {
        return rating_3_nl;
    }

    @Override
    public String toString() {
        return "Detail{" +
                "\n id=" + id +
                "\n description=" + Arrays.toString(description) +
                "\n description_nl=" + Arrays.toString(description_nl) +
                "\n details='" + details + '\'' +
                "\n details_nl='" + details_nl + '\'' +
                "\n title_1='" + title_1 + '\'' +
                "\n title_1_nl='" + title_1_nl + '\'' +
                "\n rating_1=" + Arrays.toString(rating_1) +
                "\n rating_1_nl=" + Arrays.toString(rating_1_nl) +
                "\n title_2='" + title_2 + '\'' +
                "\n title_2_nl='" + title_2_nl + '\'' +
                "\n rating_2=" + Arrays.toString(rating_2) +
                "\n rating_2_nl=" + Arrays.toString(rating_2_nl) +
                "\n title_3='" + title_3 + '\'' +
                "\n title_3_nl='" + title_3_nl + '\'' +
                "\n rating_3=" + Arrays.toString(rating_3) +
                "\n rating_3_nl=" + Arrays.toString(rating_3_nl) +
                '}';
    }
}
