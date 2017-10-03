package json.serializer;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CurrencySerializer extends JsonSerializer<Double> {

	@Override
	public void serialize(Double value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {

		NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt",
				"BR"));

		jgen.writeString(nf.format(value));
	}
}
