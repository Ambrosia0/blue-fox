import "react-router";

// declare module "react-router" {
//   interface IndexRouteObject {
//     clientLoader?: AgnosticIndexRouteObject["clientLoader"];
//     hydrate?: boolean | ((args: ClientLoaderFunctionArgs) => boolean);
//   }

//   interface NonIndexRouteObject {
//     clientLoader?: AgnosticNonIndexRouteObject["clientLoader"];
//     hydrate?: boolean | ((args: ClientLoaderFunctionArgs) => boolean);
//   }

//   interface AgnosticBaseRouteObject {
//     clientLoader?: ClientLoaderFunction;
//     hydrate?: boolean | ((args: ClientLoaderFunctionArgs) => boolean);
//   }

// }

declare global {
  interface Window {
    __staticRouterHydrationData?: any;
  }
}