package es.dws.escuela.controllers;

import es.dws.escuela.entities.Department;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DepartmentBeanInfo extends SimpleBeanInfo {
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            PropertyDescriptor id = new PropertyDescriptor("id", Department.class);
            PropertyDescriptor name = new PropertyDescriptor("name", Department.class);
            PropertyDescriptor location = new PropertyDescriptor("location", Department.class);
            return new PropertyDescriptor[]{id};
        } catch (IntrospectionException e) {
            throw new Error(e.toString());
        }
    }
}
