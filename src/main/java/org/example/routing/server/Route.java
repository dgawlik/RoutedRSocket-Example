package org.example.routing.server;

import lombok.Value;

@Value
public class Route{
    String path;
    int order;
    MappingFunction mapping;
}