package com.tvd12.ezyhttp.server.graphql;

public class GraphQLQueryDefinition extends GraphQLField {
    
    protected GraphQLQueryDefinition(Builder builder) {
        super(builder);
    }
    
    public String getName() {
        if (this.name == null) {
            throw new IllegalArgumentException("Must provide queryName!");
        }
        
        return this.name;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder extends GraphQLField.Builder {
        
        @Override
        public Builder name(String name) {
            return (Builder)super.name(name);
        }
        
        @Override
        public Builder addField(GraphQLField field) {
            return (Builder)super.addField(field);
        }
        
        @Override
        public GraphQLQueryDefinition build() {
            return new GraphQLQueryDefinition(this);
        }
    }
}
