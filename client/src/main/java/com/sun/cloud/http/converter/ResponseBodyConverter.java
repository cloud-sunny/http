package com.sun.cloud.http.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created on 2021/4/15
 * <p>
 *
 * @author sunxiaoyun
 */
public final class ResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final Feature[] EMPTY_SERIALIZER_FEATURES = new Feature[0];
    private final Type type;
    private final ParserConfig parserConfig;
    private final int featureValues;
    private final Feature[] features;


    public ResponseBodyConverter(Type type, ParserConfig config, int featureValues,
                                 Feature... features) {
        this.type = type;
        this.parserConfig = config;
        this.featureValues = featureValues;
        this.features = features;
    }

    public ResponseBodyConverter(Type type) {
        this.type = type;
        this.parserConfig = ParserConfig.getGlobalInstance();
        this.featureValues = JSON.DEFAULT_PARSER_FEATURE;
        this.features = null;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            return JSON.parseObject(value.string()
                    , type
                    , parserConfig
                    , featureValues
                    , features != null
                            ? features
                            : EMPTY_SERIALIZER_FEATURES
            );
        } finally {
            value.close();
        }
    }
}
