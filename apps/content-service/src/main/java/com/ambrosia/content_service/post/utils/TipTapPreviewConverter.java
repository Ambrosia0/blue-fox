package com.ambrosia.content_service.post.utils;

import java.io.ByteArrayOutputStream;
import java.util.Set;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.core.PreviewConverter;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.ObjectMapper;

@Component
public class TipTapPreviewConverter implements PreviewConverter{
    private final ObjectMapper objectMapper;
    private final int imageLimit;
    private final int charLimit;
    private Set<String> mandatoryFields = Set.of("attrs", "marks");

    public TipTapPreviewConverter(
        ObjectMapper objectMapper, 
        @Value("${app.default.preview-char-limit}") int charLimit,
        @Value("${app.default.preview-image-limit}") int imageLimit){
        this.objectMapper = objectMapper;
        this.imageLimit = imageLimit;
        this.charLimit = charLimit;
    }

    public String convert(String doc) throws JacksonException {
        var os = new ByteArrayOutputStream();
        var parser = objectMapper.createParser(doc);
        var gen = objectMapper.createGenerator(os);
        var structureStack = new Stack<JsonToken>();
        short limitCounter = 0;
        short imageCounter = 0;
        var isText = false;
        while(parser.nextToken() != null){
            var token = parser.currentToken();
            switch (token) {
                case START_ARRAY ->{
                    gen.writeStartArray();
                    structureStack.push(token);
                }
                case END_ARRAY ->{
                    gen.writeEndArray();
                    structureStack.pop();
                }
                case START_OBJECT ->{
                    structureStack.push(token);
                    parser.nextToken();
                    if(parser.currentToken() == JsonToken.PROPERTY_NAME && parser.getValueAsString().equals("type")){
                        parser.nextToken();
                        var currentVal = parser.getValueAsString();
                        if(currentVal.equals("image") && imageCounter >= imageLimit){
                            parser.skipChildren();
                            structureStack.pop();
                            break;
                        }
                        else{
                            if(currentVal.equals("text")){
                                isText = true;
                            }else if(currentVal.equals("image")){
                                imageCounter++;
                            }
                            gen.writeStartObject();
                            gen.writeName("type");
                            gen.writeString(currentVal);
                        }
                    }else{
                        gen.writeStartObject();
                        gen.copyCurrentEvent(parser);
                    }
                }
                case END_OBJECT ->{
                    gen.writeEndObject();
                    structureStack.pop();
                }
                case PROPERTY_NAME ->{
                    gen.writeName(parser.getValueAsString());
                }
                case VALUE_FALSE, 
                    VALUE_TRUE, 
                    VALUE_NUMBER_FLOAT, 
                    VALUE_NUMBER_INT, 
                    NOT_AVAILABLE, 
                    VALUE_NULL, 
                    VALUE_EMBEDDED_OBJECT ->{
                    gen.copyCurrentEvent(parser);
                }
                case VALUE_STRING ->{
                    var stringVal = parser.getValueAsString();
                    if(isText){
                        if(limitCounter+stringVal.length() >= charLimit){
                            var len = stringVal.length();
                            gen.writeString(stringVal.substring(0, len - ((limitCounter+len)%charLimit)));
                            return breakParsing(gen, parser, structureStack, os);
                        }else{
                            limitCounter += stringVal.length();
                            gen.writeString(stringVal);
                            isText = false;
                        }
                    }else{
                        gen.writeString(stringVal);
                    }
                }
            }
        }
        gen.close();
        return os.toString();
    }

    private String breakParsing(JsonGenerator gen, JsonParser parser, Stack<JsonToken> structureStack, ByteArrayOutputStream os){
        copyMandatory(gen, parser, structureStack, os);
        while (!structureStack.isEmpty()) {
            var breakToken = structureStack.pop();
            if(breakToken == JsonToken.START_ARRAY){
                gen.writeEndArray();
            }else if(breakToken == JsonToken.START_OBJECT){
                gen.writeEndObject();
            }
        }
        gen.close();
        return os.toString();
    };

    private void copyMandatory(JsonGenerator gen, JsonParser parser, Stack<JsonToken> structureStack, ByteArrayOutputStream os){
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            var token = parser.currentToken();
            if(token == JsonToken.PROPERTY_NAME){
                var fieldName = parser.getValueAsString();
                parser.nextToken();
                if(mandatoryFields.contains(fieldName)){
                    gen.writeName(fieldName);
                    copyFields(gen, parser);
                }else{
                    parser.skipChildren();
                }
            }
        }
    }

    private void copyFields(JsonGenerator gen, JsonParser parser){
        var token = parser.currentToken();
        switch (token) {
            case START_OBJECT ->{
                gen.writeStartObject();
                while(parser.nextToken() != JsonToken.END_OBJECT){
                    copyFields(gen, parser);
                }
                gen.writeEndObject();
            }
            case START_ARRAY ->{
                gen.writeStartArray();
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    copyFields(gen, parser);
                }
                gen.writeEndArray();
            }
            default ->{
                gen.copyCurrentEvent(parser);
            }
        }
    }
}
