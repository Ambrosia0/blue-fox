import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import FavoriteIcon from '@mui/icons-material/Favorite';
import { SvgIconProps } from '@mui/material';

export const BlankLike = (props?: SvgIconProps) => 
    <FavoriteBorderIcon
        fontSize="small"
        {...props}    
        className="active"
        sx={{
            transition: 'transform 0.3s ease'
        }}
    />
        
export const FilledLike = (props?: SvgIconProps) =>
    <FavoriteIcon
        fontSize="small"
        {...props}
        className="active"
        sx={{
            transition: 'transform 0.3s ease'
        }} 
        color="error"
    />