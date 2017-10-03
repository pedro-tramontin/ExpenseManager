package json.serializer;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CurrencyDeserializer extends JsonDeserializer<Double> {

	@Override
	public Double deserialize(JsonParser parser, DeserializationContext context)
			throws IOException, JsonProcessingException {

		NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt",
				"BR"));

		try {
			return nf.parse(parser.getText()).doubleValue();
		} catch (ParseException e) {
			throw new JsonParseException(e.getMessage(),
					parser.getCurrentLocation());
		}
	}
}
