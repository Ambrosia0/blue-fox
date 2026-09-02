import { useCallback, useEffect, useRef, useState } from "react";
import { Box } from "@mui/material"
import { PostPreview } from "../../post/components/preview/PostPreview";
import { getPosts, likePost, Post, unlikePost } from "../../../services/user/userPostApi";
import { CurrentUserProfile, PublicUserProfile } from "../../../services/user/userProfileApi";

type ProfilePostContainerProps = {
    user: PublicUserProfile | CurrentUserProfile;
}

export const ProfilePostContainer: React.FC<ProfilePostContainerProps> = ({user}) =>{
    const observerRef = useRef<IntersectionObserver | null>(null);
    const lastPostRef = useRef<HTMLDivElement | null>(null);
    const sentientRef = useRef<HTMLDivElement | null>(null);
    const [hasMore, setHasMore] = useState<boolean>(true);
    const [loading, setLoading] = useState<boolean>(false);
    const [entityResponsed,setEntityResponsed] = useState<number>(0);
    const [posts, setPosts] = useState<Post[]>([]);
    const stateRef = useRef({ posts, loading, hasMore });

    const lastElementRef = useCallback(
        (node: HTMLDivElement | null) =>{
            if(observerRef.current)
                observerRef.current.disconnect();
            observerRef.current = new IntersectionObserver(([entry]) =>{
                if(entry.isIntersecting){
                    fetchPostsForUser();
                }
            });
            if(node)
                observerRef.current.observe(node);
            lastPostRef.current = node;
        },[]);

    async function toggleLike(postId: number) {
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

    async function fetchPostsForUser() {
        const { posts: currentPosts, loading: isLoading, hasMore: canLoad} = stateRef.current;
        if(!canLoad || isLoading)
            return;
        try {
            const data = await getPosts(
                posts.length === 0?
                    {
                        authorId: user.id
                    }:
                    {
                        authorId: user.id,
                        lastSeenId: posts[posts.length].post.id,
                        lastSeenInstant: posts[posts.length].post.publishedAt
                    }
            );
            setEntityResponsed(data.length);
            setPosts((prev) =>[...prev, ...data]);
        } catch (error) {
            console.log(error);
        }
    }

    useEffect(() =>{
        fetchPostsForUser();
    }, [])

    return(
        <Box>
            {posts.map((post, idx) => {
                const isLast = idx === posts.length - 1;
                return <PostPreview 
                    key={post.post.id} 
                    {...post} 
                    toggleLike={toggleLike} 
                    ref={isLast && entityResponsed !== 0? lastElementRef: null}/>
            })}
        </Box>
    )
}