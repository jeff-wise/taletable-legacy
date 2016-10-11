
package com.kispoko.tome.sheet.group;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.sheet.Group;

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
            frames.add(new Frame(component.getId(), 100));
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



    /**
     * Load a Group from the database.
     * @param database The sqlite database object.
     * @param groupConstructorId The id of the async group constructor.
     * @param groupId The database id of the group to load.
     */
    public static void load(final SQLiteDatabase database,
                            final Integer groupConstructorId,
                            final Integer groupId)
    {
        new AsyncTask<Void,Void,Void>()
        {

            protected Void doInBackground(Void... args)
            {
                // Query Group Data
                String layoutQuery =
                    "SELECT row.index, frame.component_id, frame.size " +
                    "FROM GroupLayout layout " +
                    "INNER JOIN GroupRow row on GroupRow.group_layout_id = GroupLayout.group_layout_id" +
                    "INNER JOIN GroupFrame frame on ( " +
                            "GroupFrame.group_layout_frame_id = GroupRow.frame_1 or " +
                            "GroupFrame.group_layout_frame_id = GroupRow.frame_2 or " +
                            "GroupFrame.group_layout_frame_id = GroupRow.frame_3 ) " +
                    "WHERE GroupLayout.group_id = " + Integer.toString(groupId);

                Cursor layoutCursor = database.rawQuery(layoutQuery, null);

                ArrayList<Row> rows = new ArrayList<>();
                try {
                    Row currentRow = new Row(new ArrayList<Frame>());
                    int currentRowIndex = 0;
                    while (layoutCursor.moveToNext()) {
                        int rowIndex = layoutCursor.getInt(0);

                        if (rowIndex > currentRowIndex) {
                            rows.add(currentRow);
                            currentRow = new Row(new ArrayList<Frame>());
                        }

                        Integer componentId = layoutCursor.getInt(1);
                        Double size = layoutCursor.getDouble(2);
                        Frame frame = new Frame(componentId, size);

                        currentRow.addFrame(frame);
                    }
                }
                // TODO log
                finally {
                    layoutCursor.close();
                }

                Layout layout = new Layout(rows);
                Group.asyncConstructorMap.get(groupConstructorId).setLayout(layout);

                return null;
            }

        }.execute();
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

        public void addFrame(Frame frame) {
            this.frames.add(frame);
        }

    }


    public static class Frame implements Serializable
    {
        private Integer componentId;
        private double width;

        public Frame(Integer componentId, double width)
        {
            this.componentId = componentId;
            this.width = width;
        }

        public static Frame fromYaml(Map<String,Object> frameYaml)
        {
            int componentId = (int) frameYaml.get("id");
            double width = (double) frameYaml.get("width");

            return new Frame(componentId, width);
        }

        public Integer getComponentId()
        {
            return this.componentId;
        }

        public double getWidth()
        {
            return this.width;
        }
    }



}
