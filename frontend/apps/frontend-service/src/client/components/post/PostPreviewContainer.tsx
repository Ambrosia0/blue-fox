import { useEffect, useRef, useState } from "react"
import { PostPreview } from "./components/preview/PostPreview";
import { getPosts, likePost, Post, PostFilter, unlikePost } from "../../services/user/userPostApi";
import { PostSkeleton } from "./components/PostSkeleton";
import { PostEmpty } from "./components/PostEmpty";
import { useAuth } from "../../context/AuthContext";
import { Scope } from "@services/user/userCommunityApi";
import { deletePost } from "@services/user/userEditorApi";

type PostPreviewContainerProps = {
    postFilter?: PostFilter;
    scopes?: Set<Scope>;
}

export const PostPreviewContainer: React.FC<PostPreviewContainerProps> = ({
    postFilter,
    scopes
}) =>{
    const type = postFilter.searchType ?? "POPULAR";

    const sentientRef = useRef<HTMLDivElement | null>(null);
    const auth = useAuth();

    const [hasMore, setHasMore] = useState<boolean>(true);
    const [loading, setLoading] = useState<boolean>(false);
    const [posts, setPosts] = useState<Post[]>([]);
    const stateRef = useRef({ posts, loading, hasMore });

    async function toggleLike(postId: number) {
        if(!auth.user)
            await auth.login();
        const prev = posts;
        let state: boolean;

        setPosts(prev =>
            prev.map(post =>{            
                if(post.post.id === postId){
                    state = post.post.isLiked ?? false;
                    return {...post, isLiked: !post.post.isLiked}
                }else{
                    return post;
                }
            })
        );

        try {
            state!? 
                await unlikePost(postId):
                await likePost(postId);
        } catch (error) {
            setPosts(prev);
        }
    }

    async function handleDelete(postIdx: number) {
        try {
            await deletePost(posts[postIdx].post.id);
        } catch (error) {
            console.log(error);
        }
    }

    async function fetchPosts() {
        const { posts: currentPosts, loading: isLoading, hasMore: canLoad } = stateRef.current;
        if(!canLoad || isLoading)
            return;
        try {
            setLoading(true);
            const data = await getPosts(
                currentPosts.length === 0?
                    {
                        searchType: type,
                        authorId: postFilter.authorId? postFilter.authorId: undefined,
                        communityId: postFilter.communityId? postFilter.communityId: undefined,
                    }:
                    {
                        searchType: type,
                        authorId: postFilter.authorId? postFilter.authorId: undefined,
                        communityId: postFilter.communityId? postFilter.communityId: undefined,
                        lastScore: currentPosts[currentPosts.length - 1].post.score,
                        lastSeenId: currentPosts[currentPosts.length - 1].post.id,
                        lastSeenInstant: currentPosts[currentPosts.length - 1].post.publishedAt,
                    }
            );
            if(data.length === 0){
                setHasMore(false);
                return;
            }
            setPosts(prev => [...prev, ...data])
        } catch (error) {
            setHasMore(false);
            console.log(error);
        } finally{
            setLoading(false);
        }
    }

    useEffect(() =>{
        const observer = new IntersectionObserver(([entry]) =>{
            if(entry.isIntersecting)
                fetchPosts();
        }, {threshold: 0.1});
        if(sentientRef.current)
            observer.observe(sentientRef.current);
        return () => observer.disconnect();
    },[type])

    useEffect(() =>{
        stateRef.current = { posts, loading, hasMore }
    }, [posts, loading, hasMore])

    return(
        <>
            {posts.map((post, idx) => {
                    const isLast = idx === posts.length - 1;
                    return <PostPreview 
                        key={post.post.id} 
                        {...post} 
                        toggleLike={toggleLike} />
                })}
            <div ref={sentientRef} style={{height: "10px"}}></div>
            {loading && (<><PostSkeleton /> <PostSkeleton /></>) }
            {!loading && !hasMore && <PostEmpty type={type} authorId={postFilter.authorId} />}
        </>
    )
}