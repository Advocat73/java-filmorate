package ru.yandex.practicum.filmorate;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
        if (localDate != null)
            jsonWriter.value(localDate.format(formatter));
        else jsonWriter.value("null");
    }

    @Override
    public LocalDate read(final JsonReader jsonReader) throws IOException {
        String strLocalDate = jsonReader.nextString();
        if (strLocalDate.equals("null")) return null;
        return LocalDate.parse(strLocalDate, formatter);
    }
}


