
package com.kispoko.tome.component;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Layout
 */
public class Layout implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ArrayList<Row> rows;

    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Layout(Collection<Row> rows)
    {
        this.rows = new ArrayList<>(rows);
    }


    @SuppressWarnings("unchecked")
    public static Layout fromYaml(Map<String,Object> layoutYaml)
    {
        ArrayList<Map<String,Object>> rowsYaml =
                (ArrayList<Map<String,Object>>) layoutYaml.get("rows");
        ArrayList<Row> rows = new ArrayList<>();

        for (Map<String, Object> rowYaml : rowsYaml)
        {
            ArrayList<Map<String,Object>> framesYaml =
                    (ArrayList<Map<String,Object>>) rowYaml.get("frames");
            ArrayList<Frame> frames = new ArrayList<>();

            for (Map<String,Object> frameYaml : framesYaml)
            {
                frames.add(Frame.fromYaml(frameYaml)) ;
            }

            rows.add(new Row(frames));
        }

        return new Layout(rows);
    }


    public static Layout asDefault(List<Component> components)
    {
        ArrayList<Row> rows = new ArrayList<>();

        for (Component component : components)
        {
            Row row;
            ArrayList<Frame> frames = new ArrayList<>();
            frames.add(new Frame(component.getName(), 100));
            rows.add(new Row(frames));
        }

        return new Layout(rows);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    public List<Row> getRows()
    {
        return this.rows;
    }


    // > NESTED CLASSES
    // ------------------------------------------------------------------------------------------

    public static class Row implements Serializable
    {
        private ArrayList<Frame> frames;

        public Row(Collection<Frame> frames) {
            this.frames = new ArrayList<>(frames);
        }

        public List<Frame> getFrames() {
            return this.frames;
        }

    }


    public static class Frame implements Serializable
    {
        private String componentName;
        private double width;

        public Frame(String componentName, double width)
        {
            this.componentName = componentName;
            this.width = width;
        }

        public static Frame fromYaml(Map<String,Object> frameYaml)
        {
            String componentName = (String) frameYaml.get("name");
            double width = (double) frameYaml.get("width");

            return new Frame(componentName, width);
        }

        public String getComponentName()
        {
            return this.componentName;
        }

        public double getWidth()
        {
            return this.width;
        }
    }
}
