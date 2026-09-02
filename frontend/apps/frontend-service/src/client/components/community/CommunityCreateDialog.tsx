import { Box, Button, Chip, Dialog, DialogActions, DialogContent, DialogTitle, FormControlLabel, Paper, Stack, Step, StepLabel, Stepper, Switch, TextField, Typography } from "@mui/material";
import { checkSlug, createCommunity } from "@services/user/userCommunityApi";
import { enqueueSnackbar } from "notistack";
import { useEffect, useState } from "react"
import { useNavigate } from "react-router";
import { TAG_REGEXP } from "../../utils/utils";

export type CommunityCreateDialogProps = {
    open: boolean;
    closeDialog: () => void;
}

export const CommunityCreateDialog: React.FC<CommunityCreateDialogProps> = ({
    open,
    closeDialog
}) =>{
    const [displayedName, setDisplayedName] = useState<string>("");
    const [displayedNameError, setDisplayedNameError] = useState<string | null>(null);

    const [slug, setSlug] = useState<string>("");
    const [slugError, setSlugError] = useState<string | null>(null);

    const [stepError, setStepError] = useState<string | null>(null);

    const [loading, setLoading] = useState<boolean>(false);

    const [tags, setTags] = useState<string[]>([]);
    const [isPrivate, setIsPrivate] = useState<boolean>(false);
    const [step, setStep] = useState<number>(0);

    const [tagInput, setTagInput] = useState<string>("");

    const navigate = useNavigate();

    const validateDisplayedName = () =>{
        if(displayedName.length < 6)
            setDisplayedNameError("Name is too short");
        if(displayedName.length >= 40)
            setDisplayedNameError("Name is too long!");
    }

    const validateSlug = () =>{
        if(slug.length < 6){
            setSlugError("Short name is too short!");
        }
        if(slug.length >= 32)
            setSlugError("Short name is too long!");
    }

    const validatorMap: Record<number, () => boolean> = {
        0: () => true,
        1: () => {
            validateDisplayedName();
            validateSlug();
            if(slugError || displayedNameError)
                return false;
            return true;
        },
        2: () => true
    };

    const stepErrorCallbackMap: Record<number, () => void> = {
        1: () =>{
            if(displayedNameError)
                setDisplayedName("");
            if(slugError)
                setSlug("");
        }
    }

    const handleTagAdd = (tag: string) => {
        const normalized = tag.startsWith('#')? tag: `#${tag}`;
        if(!normalized.match(TAG_REGEXP))
            return;
        setTags(prev => (prev.includes(normalized) || prev.length >= 3)? prev: [...prev, normalized]);
    }

    const handleTagDelete = (tag: string) => {
        setTags(prev => prev.filter((val, _) => val !== tag));
    }

    const handleCommunityCreate = async () =>{
        try {
            const resp = await createCommunity({
                displayedName: displayedName,
                slug: slug,
                isPrivate: isPrivate,
                tags: tags
            });
            handleClose();
            navigate(`/community/${resp.slug}`);
        } catch (error) {
            enqueueSnackbar({
                variant: "error",
                message: "Can't create community!"
            });
        }
    }

    const isValid = () =>{
        if(validatorMap[step]){
            return validatorMap[step]();
        }
        return true;
    }

    const handleStepForward = () => {
        if(loading)
            return;
        if(step < 2){
            if(!isValid()){
                return;
            }
            setStep(prev => prev + 1);
        }else{
            handleCommunityCreate();
        }
    }

    const handleStepBack = () => {
        if(!isValid() && stepErrorCallbackMap[step]){
            stepErrorCallbackMap[step]();
        }
        setStepError(null);
        if(step === 0){
            handleClose();
            return;
        }
        setStep(prev => prev - 1);
    }

    const handleClose = (event?: {}, reason?: "backdropClick" | "escapeKeyDown") => {
        if(reason && reason === 'backdropClick'){
            return;
        }
        closeDialog();
    }

    useEffect(() =>{
        if(slug.length < 6)
            return;
        const timeout = setTimeout(() => {
            isSlugClaimed();
        }, 300);
        return () => clearTimeout(timeout);
    }, [slug])

    const isSlugClaimed = async () =>{
        try {
            setLoading(true);
            const claimed = await checkSlug(slug);
            if(claimed){
                setSlugError("Name already claimed!");
            }
        } catch (error) {
            setStepError("Can't create community now!");
            console.log(error);
        } finally{
            setLoading(false);
        }
    }

    return(
        <Dialog
            open={open}
            onClose={handleClose}
            disableScrollLock
        >
            <DialogTitle>Create community</DialogTitle>
            <Stepper activeStep={step} sx={{ pl: 2, pb: 1, pr: 2}}>
                <Step><StepLabel>Tags</StepLabel></Step>
                <Step><StepLabel>Name</StepLabel></Step>
                <Step><StepLabel>Privacy</StepLabel></Step>
            </Stepper>
            <DialogContent
                sx={{ overflow: 'hidden', p: 0}}
            >
                <Box sx={{
                    display: 'flex',
                    transform: `translateX(-${step * 100}%)`,
                    transition: "transform .3s ease",
                }}>
                    {/* Step 1 */}
                    <Box
                        sx={{
                            flex: "0 0 100%",
                            p: 3,
                            display: "flex",
                            flexDirection: "column",
                            gap: 2,
                            boxSizing: "border-box",
                        }}
                    >
                        <Typography variant="h6">
                            Community tags
                        </Typography>

                        <Typography
                            variant="body2"
                            color="text.secondary"
                        >
                            Add tags that describe your community. They help users discover it.
                        </Typography>

                        <TextField
                            fullWidth
                            placeholder="Type a tag and press Enter"
                            value={tagInput}
                            aria-label="Tags"
                            onChange={(e) => setTagInput(e.target.value)}
                            onKeyDown={(e) => {
                                if(e.key === 'Enter'){
                                    e.preventDefault();
                                    handleTagAdd(tagInput);
                                    setTagInput("");
                                }
                            }}
                        />

                        <Stack
                            direction="row"
                            spacing={1}
                            useFlexGap
                            flexWrap="wrap"
                        >
                            {tags.map(tag => (
                                <Chip
                                    key={tag}
                                    label={tag}
                                    color="primary"
                                    onDelete={() => handleTagDelete(tag)}
                                />
                            ))}
                        </Stack>
                    </Box>

                    {/* Step 2 */}
                    <Box
                        sx={{
                            flex: "0 0 100%",
                            p: 3,
                            display: "flex",
                            flexDirection: "column",
                            gap: 2,
                            boxSizing: "border-box"
                        }}
                    >
                        <Typography variant="h6">
                            Community name
                        </Typography>

                        <Typography
                            variant="body2"
                            color="text.secondary"
                        >
                            Pick a unique and memorable name.
                        </Typography>

                        <TextField
                            fullWidth
                            label="Name"
                            value={displayedName}
                            onChange={(e) => {
                                setDisplayedName(e.target.value)
                                setStepError(null);
                            }}
                            helperText={displayedNameError !== null? stepError: ""}
                            error={!!displayedNameError}
                        />

                        <TextField 
                            fullWidth
                            label="URL name"
                            value={slug}
                            onChange={(e) => {
                                const val = e.target.value
                                    .toLocaleLowerCase()
                                    .replace(/[^a-z0-9-_]/g, "");
                                setSlug(val);
                                setSlugError(null);
                            }}
                            helperText={slugError? slugError: `Your community URL: /communities/${slug || "your-name"}`}
                            error={!!slugError}
                        />
                    </Box>

                    {/* Step 3 */}
                    <Box
                        sx={{
                            flex: "0 0 100%",
                            p: 3,
                            display: "flex",
                            flexDirection: "column",
                            gap: 2,
                            boxSizing: "border-box",
                        }}
                    >
                        <Typography variant="h6">
                            Privacy
                        </Typography>

                        <Typography
                            color="text.secondary"
                            sx={{ mb: 2 }}
                        >
                            Private communities require approval or invitation to join.
                        </Typography>

                        <Paper
                            variant="outlined"
                            sx={{
                                p: 2,
                                borderRadius: 2,
                            }}
                        >
                            <FormControlLabel
                                control={
                                    <Switch
                                        checked={isPrivate}
                                        onChange={(e) => setIsPrivate(e.target.checked)}
                                    />
                                }
                                label="Private community"
                            />
                        </Paper>
                    </Box>

                </Box>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleStepBack}>{step !== 0? "Back": "Cancel"}</Button>
                <Button onClick={handleStepForward}>{step !== 2? "Next": "Create"}</Button>
            </DialogActions>
        </Dialog>
    )
}