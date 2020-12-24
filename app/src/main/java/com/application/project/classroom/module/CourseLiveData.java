package com.application.project.classroom.module;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.application.project.classroom.object.Course;

import java.util.List;

public class CourseLiveData extends ViewModel {

    private MutableLiveData<List<Course>> liveData;

    private List<Course> courses;



}
