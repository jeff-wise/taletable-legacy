
package com.kispoko.tome.sheet.group;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.kispoko.tome.db.SheetContract;
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

    private Long id;

    private ArrayList<Row> rows;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Layout(Collection<Row> rows)
    {
        this.id   = null;
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


    public void setId(Long id)
    {
        this.id = id;
    }


    // >> Database Methods
    // ------------------------------------------------------------------------------------------

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
                    "SELECT frame1.component_id, frame1.size, frame2.component_id, " +
                           "frame2.size, frame3.component_id, frame3.size " +
                    "FROM GroupLayout layout " +
                    "INNER JOIN GroupRow row on GroupRow.group_layout_id = GroupLayout.group_layout_id" +
                    "INNER JOIN GroupFrame frame1 on frame1.group_layout_frame_id = GroupRow.frame1 " +
                    "INNER JOIN GroupFrame frame2 on frame2.group_layout_frame_id = GroupRow.frame2 " +
                    "INNER JOIN GroupFrame frame3 on frame3.group_layout_frame_id = GroupRow.frame3  " +
                    "WHERE GroupLayout.group_id = " + Integer.toString(groupId) + " " +
                    "ORDER BY row.index ASC ";

                Cursor layoutCursor = database.rawQuery(layoutQuery, null);

                ArrayList<Row> rows = new ArrayList<>();
                try
                {
                    while (layoutCursor.moveToNext())
                    {
                        Row row = new Row(new ArrayList<Frame>());

                        Integer frame1ComponentId = layoutCursor.getInt(0);
                        Double frame1Size = layoutCursor.getDouble(1);
                        Integer frame2ComponentId = layoutCursor.getInt(2);
                        Double frame2Size = layoutCursor.getDouble(3);
                        Integer frame3ComponentId = layoutCursor.getInt(4);
                        Double frame3Size = layoutCursor.getDouble(5);

                        if (frame1ComponentId != null)
                            row.addFrame(new Frame(frame1ComponentId, frame1Size));

                        if (frame2ComponentId != null)
                            row.addFrame(new Frame(frame2ComponentId, frame2Size));

                        if (frame3ComponentId != null)
                            row.addFrame(new Frame(frame3ComponentId, frame3Size));

                        rows.add(row);
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


    /**
     * Save to the database.
     * @param database The SQLite database object.
     * @param groupId  The id of the layout's group parent.
     * @param recursive If true, save all child objects as well.
     */
    public void save(final SQLiteDatabase database, final Long groupId, final boolean recursive)
    {
        final Layout thisLayout = this;

        new AsyncTask<Void,Void,Void>()
        {
            protected Void doInBackground(Void... args)
            {
                ContentValues layoutRow = new ContentValues();
                layoutRow.put("group_layout_id", thisLayout.id);
                layoutRow.put("group_id", groupId);

                Long layoutId = database.insertWithOnConflict(SheetContract.GroupLayout.TABLE_NAME,
                                                              null,
                                                              layoutRow,
                                                              SQLiteDatabase.CONFLICT_REPLACE);

                int rowIndex = 0;
                for (Row row : thisLayout.rows)
                {
                    ContentValues layoutRowRow = new ContentValues();
                    layoutRowRow.put("group_layout_row_id", row.getId());
                    layoutRowRow.put("group_layout_id", layoutId);
                    layoutRowRow.put("index", rowIndex);

                    List<Frame> frames = row.getFrames();
                    if (frames.size() > 0) layoutRowRow.put("frame1", frames.get(0).getId());
                    if (frames.size() > 1) layoutRowRow.put("frame2", frames.get(1).getId());
                    if (frames.size() > 2) layoutRowRow.put("frame3", frames.get(2).getId());

                    Long layoutRowId  = database.insertWithOnConflict(
                                                    SheetContract.GroupLayoutRow.TABLE_NAME,
                                                    null,
                                                    layoutRowRow,
                                                    SQLiteDatabase.CONFLICT_REPLACE);

                    row.setId(layoutRowId);

                    for (Frame frame : row.getFrames())
                    {
                        ContentValues layoutFrameRow = new ContentValues();
                        layoutFrameRow.put("group_layout_frame_id", frame.getId());
                        layoutFrameRow.put("component_id", frame.getComponentId());
                        layoutFrameRow.put("size", frame.getWidth());

                        Long layoutFrameId = database.insertWithOnConflict(
                                                    SheetContract.GroupLayoutFrame.TABLE_NAME,
                                                     null,
                                                     layoutFrameRow,
                                                     SQLiteDatabase.CONFLICT_REPLACE);
                        frame.setId(layoutFrameId);
                    }

                    rowIndex += 1;
                }

                thisLayout.setId(layoutId);

                return null;
            }


        }.execute();

    }



    // > NESTED CLASSES
    // ------------------------------------------------------------------------------------------

    public static class Row implements Serializable
    {
        private ArrayList<Frame> frames;

        private Long id;

        public Row(Collection<Frame> frames) {
            this.frames = new ArrayList<>(frames);
            this.id = null;
        }

        public Long getId() {
            return this.id;
        }

        public void setId(Long id) {
            this.id = id;
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
        private Long id;

        private Integer componentId;
        private double width;

        public Frame(Integer componentId, double width)
        {
            this.componentId = componentId;
            this.width = width;
            this.id = null;
        }

        public static Frame fromYaml(Map<String,Object> frameYaml)
        {
            int componentId = (int) frameYaml.get("id");
            double width = (double) frameYaml.get("width");

            return new Frame(componentId, width);
        }

        public Long getId()
        {
            return this.id;
        }

        public void setId(Long id)
        {
            this.id = id;
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
