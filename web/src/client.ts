import { GraphQLClient } from "graphql-request";

const API_URL = `/graphql`;

export const graphQLClient = new GraphQLClient(API_URL, {
  headers: {
    // Authorization: `Bearer ${process.env.API_KEY}`
  }
});
