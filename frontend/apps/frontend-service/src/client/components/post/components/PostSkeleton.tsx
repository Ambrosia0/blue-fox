import { Skeleton, Box, Divider } from "@mui/material" 

export const PostSkeleton = () =>{
    return(
        <Box display="flex" flexDirection="column">
            <Box display="flex" flexDirection="row">
                <Skeleton sx={{width: 50, height: 50}} variant="circular" />
                <Box flex={1} flexDirection="row" pl={2}>
                    <Skeleton sx={{maxWidth: 200, mb: 1}} variant="rectangular" />
                    <Skeleton sx={{maxWidth: 400}} variant="rectangular" />
                </Box>
            </Box>
            <Box>
                <Skeleton variant="text" sx={{borderRadius: '10px', minHeight: 60, maxWidth:500}} />
                <Skeleton variant="rectangular" sx={{minHeight: 200, borderRadius: '5px'}} />
            </Box>
            <Divider sx={{mt:1}} />
            <Box display="flex" flexDirection="row" justifyContent='space-between' >
                <Box display="flex" flexDirection="row" p={1}>
                    <Skeleton variant="rectangular" sx={{minWidth: 40, minHeight: 40, borderRadius: '20px'}}/>
                    <Skeleton variant="rectangular" sx={{minWidth: 40, minHeight: 40, borderRadius: '20px', ml: 2}}/>
                </Box>
                <Box>
                    <Skeleton variant="text" sx={{minWidth: 120, minHeight: 40, pr: 2}} />
                </Box>
            </Box>
        </Box>
    )
}