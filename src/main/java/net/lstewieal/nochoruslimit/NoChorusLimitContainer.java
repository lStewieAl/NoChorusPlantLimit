package net.lstewieal.nochoruslimit;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

import java.util.ArrayList;
import java.util.List;

public class NoChorusLimitContainer extends DummyModContainer {
    public NoChorusLimitContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "nochoruslimit";
        meta.name = "No Chorus Plant Limit";
        meta.description = "Remove the limit on chorus plant growth.";
        meta.version = "1.12.2-1.0";
        List<String> authors = new ArrayList<>(); authors.add("lStewieAl");
        meta.authorList = authors;
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController l) {
        bus.register(this);
        return true;
    }
}
