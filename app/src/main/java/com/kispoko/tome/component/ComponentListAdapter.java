
package com.kispoko.tome.component;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import static com.kispoko.tome.component.Component.Type.IMAGE;



/**
 * RecyclerView Adapter for a list of components displayed in a section of a sheet.
 */
public class ComponentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private ArrayList<Component> components;

    private Context context;


    public ComponentListAdapter(Context context, ArrayList<Component> components)
    {
        this.components = components;
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        Component.Type componentType = Component.Type.values()[viewType];

        switch (componentType)
        {
            case TEXT:
                View textCompView = Text.getView(context);
                viewHolder = new TextComponentViewHolder(textCompView);
                break;
            case IMAGE:
                View imageCompView = Image.getView(context);
                viewHolder = new ImageComponentViewHolder(imageCompView);
                break;
        }

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        Component.Type componentType = Component.Type.values()[viewHolder.getItemViewType()];

        switch (componentType)
        {
            case TEXT:
                TextComponentViewHolder textCompVH = (TextComponentViewHolder) viewHolder;
                configureTextComponentViewHolder(textCompVH, position);
                break;
            case IMAGE:
                ImageComponentViewHolder imageCompVH = (ImageComponentViewHolder) viewHolder;
                configureImageComponentViewHolder(imageCompVH, position);
                break;
        }
    }


    @Override
    public int getItemViewType(int position)
    {
        Component.Type componentType =  components.get(position).getType();

        switch (componentType)
        {
            case TEXT:
                return Component.Type.TEXT.ordinal();
            case IMAGE:
                return Component.Type.IMAGE.ordinal();
        }

        return -1;
    }


    @Override
    public int getItemCount()
    {
        return this.components.size();
    }


    public class TextComponentViewHolder extends RecyclerView.ViewHolder
    {

        public TextComponentViewHolder(View itemView)
        {
            super(itemView);
        }
    }


    public class ImageComponentViewHolder extends RecyclerView.ViewHolder
    {
        public ImageComponentViewHolder(View itemView)
        {
            super(itemView);
        }
    }


    private void configureTextComponentViewHolder(TextComponentViewHolder viewHolder,
                                                  int position)
    {
        this.components.get(position).configureView(viewHolder.itemView);
    }


    private void configureImageComponentViewHolder(ImageComponentViewHolder viewHolder,
                                                  int position)
    {
        this.components.get(position).configureView(viewHolder.itemView);
    }
}
