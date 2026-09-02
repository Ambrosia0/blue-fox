import { Container } from "@mui/material"
import { Post } from "./components/Post";
import { useLoaderData, useNavigation, useSearchParams } from "react-router";
import { useEffect, useState } from "react";
import { Post as PostEntity, PostContentResponse, likePost, unlikePost, getLike } from "@services/user/userPostApi";
import { getUserInfo } from "@services/user/userProfileApi";
import { PostSkeleton } from "./components/PostSkeleton";
import { ErrorDisplay } from "../ErrorDisplay";
import { NotificationPayload, NotificationType, useNotification } from "../../context/NotificationEvent";
import { useAuth } from "../../context/AuthContext";


export const PostContainer = () =>{
    const preLoadPost = useLoaderData<PostContentResponse>();
    const [params] = useSearchParams();
    const [post, setPost] = useState<PostEntity>(undefined);
    const notification = useNotification();
    const auth = useAuth();
    const navigation = useNavigation();

    useEffect(() => {
        dataAggregation();

        notification?.setFilter({
            postId: post?.post.id!
        });

        notification?.eventTarget.addEventListener(NotificationType.PostLike, (event) =>{
            const data = (event as MessageEvent).data as NotificationPayload[typeof NotificationType.PostLike];
            if(post){
                setPost({
                    ...post,
                    post: {
                        ...post.post,
                        likeCount: post.post.likeCount +=data[post.post.id]
                    }
                });
            }
        })

        notification?.eventTarget.addEventListener(NotificationType.CommentCreation, (event) =>{
            setPost(prev => ({
                ...prev,
                post: {
                    ...prev.post,
                    commentCount: prev.post.commentCount + 1
                }
            }))
        })

        return () => notification?.setFilter({
            postId: null
        });
    }, [])

    async function dataAggregation(){
        Promise.allSettled([getUserInfo([preLoadPost.authorId])])
            .then( ([userDataRes]) =>{
                const userData = userDataRes.status === "fulfilled"?
                    userDataRes.value:
                    undefined;
                setPost(prev => ({
                    post: (prev?.post)? prev.post: preLoadPost,
                    user: userData?.get(preLoadPost.authorId) ?? {avatarId: "", id: preLoadPost.authorId, username: ""},
                }))
            });
    }

    async function checkLike() {
        try {
            const isLiked = await getLike(preLoadPost.id);
            setPost(prev => {
                return prev?
                    {
                        ...prev,
                        post:{
                            ...prev.post,
                            isLiked: isLiked
                        }
                    }:
                    {
                        post: {
                            ...preLoadPost,
                            isLiked: isLiked
                        },
                        user: {avatarId: "", id: preLoadPost.authorId, username: ""}
                    }
            })
        } catch (error) {
            console.log(error);
        }
    }

    const handleLikeToggle = async () =>{
        if(!auth.user)
            await auth.login();
        const initialState = post.post;
        setPost(prev =>({
            ...prev,
            post: {
                ...prev.post,
                isLiked: !initialState.isLiked,
                likeCount: initialState.isLiked? 
                    initialState.likeCount - 1: 
                    initialState.likeCount + 1
            }
        }))
        try {
            initialState.isLiked? 
                unlikePost(post.post.id): 
                likePost(post.post.id);
        } catch (error) {
            setPost(prev => ({
                ...prev,
                post: initialState
            }));
        }
    }
    
    useEffect(() =>{
        const section = params.get("section");
        if(section && section === "comments"){
            const el = document.getElementById("comment-section");
            el?.scrollIntoView({behavior: 'smooth'});
        } 
    },[]);

    useEffect(() =>{
        if(auth.user)
            checkLike();
        else
            setPost(prev => ({...prev, post: {...prev.post, isLiked: false}}))
    }, [auth.user])
    
    return(
        <div>
            {navigation.state === 'loading'?
                <PostSkeleton />:
                (post? 
                    <Post {...post} toggleLike={handleLikeToggle}/>:
                    <Container>
                        <ErrorDisplay />
                    </Container>
                )
            }
        </div>
    )
}