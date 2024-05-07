package one.oth3r.directionhud.common.utils;

import com.google.gson.Gson;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;

public class Dest extends Loc {
    private String name = null;
    private String color = null;

    public Dest() {
        super();
    }

    /**
     * creates Dest with x, y, z, dimension, name, & color
     */
    public Dest(Integer x, Integer y, Integer z, String dimension, String name, String color) {
        super(x,y,z,dimension);
        this.name = name;
        this.color = color;
    }

    public Dest(Dest dest) {
        super(dest);
        name = dest.name;
        color = dest.color;
    }

    public Dest(Player player, String name, String color) {
        super(player);
        this.name = name;
        this.color = color;
    }

    public Dest(Loc loc, String name, String color) {
        super(loc);
        this.name = name;
        this.color = color;
    }

    public Dest(String dest) {
        super(new Gson().fromJson(dest, Dest.class));
        Dest data = new Gson().fromJson(dest, Dest.class);
        this.name = data.name;
        this.color = data.color;
    }

    public boolean hasDestRequirements() {
        return hasXYZ() && getDimension() != null && this.name != null && this.color != null;
    }

    @Override
    public CTxT getBadge() {
        CTxT msg = CTxT.of("");
        // if there's a dimension, add a dimension badge to the start of the message
        if (this.getDimension() != null) msg.append(Dimension.getBadge(getDimension())).append(" ");
        // if there's a name, make the badge the name, e.g. [O] name
        if (this.name != null) msg.append(CTxT.of(this.name).color(this.color==null?"#ffffff":this.color)
                .hEvent(CTxT.of(getXYZ())));
            // no name, just have the coordinates
        else msg.append(CTxT.of(getXYZ()));
        return msg;
    }
    /**
     * creates a Dest badge without the name
     * @return badge
     */
    public CTxT getNamelessBadge() {
        CTxT msg = CTxT.of("");
        msg.append(Dimension.getBadge(getDimension())).append(" ");
        msg.append(CTxT.of(getXYZ()).hEvent(CTxT.of(this.name).color(this.color==null?"#ffffff":this.color)));
        return msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
