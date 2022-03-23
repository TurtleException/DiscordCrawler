package de.eldritch.discord.turtlecrawler.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;

public class RestActionUtil {
    public static RestAction<DataObject> getDataObject(JDA jda, Route.CompiledRoute route) {
        return new RestActionImpl<>(jda, route, (response, dataObjectRequest) -> response.getObject());
    }

    public static RestAction<DataArray> getDataArray(JDA jda, Route.CompiledRoute route) {
        return new RestActionImpl<>(jda, route, (response, dataObjectRequest) -> response.getArray());
    }
}
