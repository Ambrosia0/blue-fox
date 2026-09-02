import { Box, Typography } from "@mui/material";
import { CommunityEventFilter, CommunityPreview as CommunityPreviewType, getCommunities } from "@services/user/userCommunityApi";
import React, { useEffect, useRef, useState } from "react";
import { CommunityPreview as CommunityPreviewCard } from "../community/CommunityPreview";
import { CommunityCardSkeleton } from "../community/components/CommunityCardSkeleton";
import { useTranslation } from "react-i18next";

export const CommunitySearchContainer: React.FC<CommunityEventFilter> = ({
    searchString,
    tags
}) =>{
    const [communities, setCommunities] = useState<CommunityPreviewType[]>([]);
    const [hasMore, setHasMore] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(false);
    const stateRef = useRef({ communities, loading, hasMore });
    const sentientRef = useRef<HTMLDivElement | null>(null);

    async function fetchCommunities(){
        const {communities: currentCommunities, loading: isLoading, hasMore: canLoad} = stateRef.current;
        if(!canLoad || isLoading)
            return;
        try {
            setLoading(true);
            const data = await getCommunities(
                currentCommunities.length === 0?
                {
                    tags: tags,
                    searchString: searchString
                }:
                {
                    tags: tags,
                    searchString:searchString,
                    lastSeenId: currentCommunities[currentCommunities.length - 1].id,
                    lastSeenScore: currentCommunities[currentCommunities.length - 1].score
                }
            );
            if(data.length === 0){
                setHasMore(false);
                return;
            }

            setCommunities(prev => [...prev, ...data]);
        } catch (error) {
            console.log(error);
        } finally{
            setLoading(false);
        }
    }

    useEffect(() => {
        const observer = new IntersectionObserver(([entry]) =>{
            if(entry.isIntersecting)
                fetchCommunities();
        }, {threshold: 0.1});
        if(sentientRef.current)
            observer.observe(sentientRef.current);
        return () => observer.disconnect();
    }, []);

    useEffect(() =>{
        stateRef.current = { communities, loading, hasMore };
    }, [communities, loading, hasMore]);

    const { t } = useTranslation();

    return (
        <Box sx={{ p: 2 }}>
            {communities.map((communityPreview, _) => (
                <CommunityPreviewCard key={communityPreview.id} community={communityPreview} />
            ))}
            <div ref={sentientRef} style={{height: "10px"}} />
            {loading && (
                <>
                    {[1, 2].map((i) => (
                        <CommunityCardSkeleton key={i} />
                    ))}
                </>
            )}
            {!hasMore && communities.length > 0 && (
                <Typography variant="body2" color="text.secondary" align="center" sx={{ mt: 2 }}>
                    {t("search.noResults")}
                </Typography>
            )}
            {communities.length === 0 && !loading && (
                <Typography variant="body1" color="text.secondary" align="center" sx={{ mt: 4 }}>
                    {t("search.noResults")}
                </Typography>
            )}
        </Box>
    );
}
