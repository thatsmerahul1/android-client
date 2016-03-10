package com.ecarezone.android.patient.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * Created by L&T Technology Services on 2/26/2016.
 */
public class NewsCategory implements Serializable {
    @Expose
    public String newsCategory;
    @Expose
    public String newsImageLink;
    @Expose
    public List<News> newsAbstractList;
}