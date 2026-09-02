import type { FetcherWithComponents } from "react-router";

export type LikeSetterProp = {
    toggleLike: () => void;
}

export type PreviewLikeSetterProp = {
    toggleLike: (postId: number) => void;
}

export type LikeFetcherProp = {
    fetcher: FetcherWithComponents<any>;
}