package gov.adlnet.xapi.model.adapters;

import java.lang.reflect.Type;
import com.google.gson.*;
import gov.adlnet.xapi.model.*;

public class ActorAdapter implements JsonDeserializer<Actor>,
		JsonSerializer<Actor> {
	@Override
	public Actor deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject obj = json.getAsJsonObject();
		Class<?> klass = null;
		try {
			if (obj.get("objectType").toString().toLowerCase() == "agent") {
				klass = Class.forName("gov.adlnet.xapi.model.Agent");
			} else {
				klass = Class.forName("gov.adlnet.xapi.model.Group");
			}
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e.getMessage());
		}
		return context.deserialize(json, klass);
	}

	@Override
	public JsonElement serialize(Actor a, Type typeofa,
			JsonSerializationContext context) {
		return a.serialize();
	}
}
