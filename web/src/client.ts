import { GraphQLClient } from "graphql-request";

export const API_URL = `/graphql`;

export const graphQLClient = new GraphQLClient(API_URL, {
  headers: {
    // Authorization: `Bearer ${process.env.API_KEY}`
  }
});
