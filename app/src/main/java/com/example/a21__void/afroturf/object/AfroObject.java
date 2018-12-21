package com.example.a21__void.afroturf.object;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.a21__void.afroturf.AfroJson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by ASANDA on 2018/09/30.
 * for Pandaphic
 */
public abstract class AfroObject {

    public abstract String getName();
    public abstract String getUID();
    public abstract void set(JsonParser parser, JsonElement jsonElement);
    public abstract JsonElement asJson();

    public void set(JsonParser parser, String json){ this.set(parser, parser.parse(json)); }

    public static abstract class UIHandler extends RecyclerView.ViewHolder{

        public UIHandler(View itemView) {
            super(itemView);
        }

        public abstract void bind(AfroObject afroObject, int position);
        public abstract Class<? extends AfroObject> getObjectClass();
    }

    @Override
    public String toString() {
        return this.asJson().toString();
    }
}
