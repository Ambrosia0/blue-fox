import { Box, Card, CardContent, Skeleton } from "@mui/material"

export const CommunityCardSkeleton = () => {
    return(
        <Card sx={{ mb: 2, borderRadius: 2 }}>
            <CardContent sx={{ display: "flex", alignItems: "center", p: 2, py: 1.5 }}>
                <Skeleton
                    variant="rectangular"
                    width={56}
                    height={56}
                    sx={{ mr: 2, borderRadius: 2 }}
                />
                <Box sx={{ flex: 1 }}>
                    <Skeleton width="60%" height={24} />
                    <Skeleton width="40%" height={20} sx={{ mt: 1 }} />
                </Box>
            </CardContent>
        </Card>
    )
}