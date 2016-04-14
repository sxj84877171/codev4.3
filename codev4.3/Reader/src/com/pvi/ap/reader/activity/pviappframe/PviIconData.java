/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pvi.ap.reader.activity.pviappframe;

public class PviIconData implements Cloneable {
    /**
     * Indicates ths item represents a piece of text.
     */
    public static final int TEXT = 1;
    
    /**
     * Indicates ths item represents an icon.
     */
    public static final int ICON = 2;

    /**
     * The type of this item. One of TEXT, ICON, or LEVEL_ICON.
     */
    public int type;

    /**
     * The slot that this icon will be in if it is not a notification
     */
    public String slot;

    /**
     * The package containting the icon to draw for this item. Valid if this is
     * an ICON type.
     */
    public String iconPackage;
    
    /**
     * The icon to draw for this item. Valid if this is an ICON type.
     */
    public int iconId;
    
    /**
     * The level associated with the icon. Valid if this is a LEVEL_ICON type.
     */
    public int iconLevel;
    
    /**
     * The "count" number.
     */
    public int number;

    /**
     * The text associated with the icon. Valid if this is a TEXT type.
     */
    public CharSequence text;
    
    public boolean isVisble;    //是否可见
    public int width;           //显示区的宽度
    public int top;             //相对于顶部的距离

    private PviIconData() {
    }

    public static PviIconData makeIcon(String slot,
            String iconPackage, int iconId, int iconLevel, int number,int top,int width,boolean isVisble) {
        PviIconData data = new PviIconData();
        data.type = ICON;
        data.slot = slot;
        data.iconPackage = iconPackage;
        data.iconId = iconId;
        data.iconLevel = iconLevel;
        data.number = number;
        data.top = top;
        data.width = width;
        data.isVisble = isVisble;
        return data;
    }
    
    public static PviIconData makeText(String slot, CharSequence text) {
        PviIconData data = new PviIconData();
        data.type = TEXT;
        data.slot = slot;
        data.text = text;
        return data;
    }

    public void copyFrom(PviIconData that) {
        this.type = that.type;
        this.slot = that.slot;
        this.iconPackage = that.iconPackage;
        this.iconId = that.iconId;
        this.iconLevel = that.iconLevel;
        this.number = that.number;
        this.top = that.top;
        this.width = that.width;
        this.isVisble = that.isVisble;
        this.text = that.text; // should we clone this?
    }

    public PviIconData clone() {
        PviIconData that = new PviIconData();
        that.copyFrom(this);
        return that;
    }

    public String toString() {
        if (this.type == TEXT) {
            return "IconData(slot=" + (this.slot != null ? "'" + this.slot + "'" : "null")
                    + " text='" + this.text + "')"; 
        }
        else if (this.type == ICON) {
            return "IconData(slot=" + (this.slot != null ? "'" + this.slot + "'" : "null")
                    + " package=" + this.iconPackage
                    + " iconId=" + Integer.toHexString(this.iconId)
                    + " iconLevel=" + this.iconLevel + ")"; 
        }
        else {
            return "IconData(type=" + type + ")";
        }
    }
}
