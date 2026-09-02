import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    DialogContentText,
    Button,
    FormControl,
    FormHelperText,
    InputLabel,
    Select,
    SelectChangeEvent,
    TextField,
    Box,
    Typography,
    MenuItem,
} from "@mui/material";
import { useEffect, useState } from "react";
import { ReportReason, getReportReasons, TargetType, ReportRequest, sendReport } from "@services/user/userReportApi";
import { useTranslation } from "react-i18next";

interface ReportDialogProps {
    open: boolean;
    onClose: () => void;
    onSuccess: () => void;
    targetType: TargetType;
    targetId: string;
}

export const ReportDialog = ({ open, onClose, onSuccess, targetType, targetId }: ReportDialogProps) => {
    const [reasons, setReasons] = useState<ReportReason[]>([]);
    const [selectedReason, setSelectedReason] = useState<number>(0);
    const [reportContent, setReportContent] = useState<string>("");
    const [error, setError] = useState<boolean>(false);
    const [errorMsg, setErrorMsg] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(false);
    const { t } = useTranslation();

    useEffect(() => {
        if (open) {
            getReportReasons().then((data) => {
                setReasons(data);
            });
            setSelectedReason(0);
            setReportContent("");
            setError(false);
            setErrorMsg("");
        }
    }, [open]);

    const handleSubmit = async () => {
        if (selectedReason === 0) {
            setError(true);
            setErrorMsg(t("report.reportDialog.reasonRequired"));
            return;
        }
        if (reportContent.trim() === "") {
            setError(true);
            setErrorMsg(t("report.reportDialog.descriptionRequired"));
            return;
        }

        setLoading(true);
        try {
            const request: ReportRequest = {
                reportReasonId: selectedReason,
                targetType: targetType,
                reportContent: reportContent,
                reportContentKey: targetId,
            };
            sendReport(request);
            onSuccess();
        } catch (e) {
            setError(true);
            setErrorMsg(t("report.reportDialog.error"));
        } finally {
            setLoading(false);
        }
    };

    const handleClose = () => {
        onClose();
    };

    const handleReasonChange = (event: SelectChangeEvent) => {
        setSelectedReason(Number(event.target.value));
        setError(false);
        setErrorMsg("");
    };

    const reasonText = reasons.find((r) => r.id === selectedReason)?.title || t("report.reportDialog.reasonPlaceholder");

    return (
        <Dialog
            open={open}
            onClose={handleClose}
            maxWidth="sm"
            fullWidth
            fullScreen={false}
            disableScrollLock
        >
            <DialogTitle>
                <Typography variant="h6" component="div">
                    {t("report.reportDialog.title")}
                </Typography>
            </DialogTitle>
            <DialogContent>
                <DialogContentText sx={{ mb: 2 }}>
                    {t("report.reportDialog.description")}
                </DialogContentText>

                <Box sx={{ mt: 2 }}>
                    <FormControl error={error} fullWidth sx={{ mb: 2 }}>
                        <InputLabel id="report-reason-label">{t("report.reportDialog.reasonLabel")}</InputLabel>
                        <Select
                            labelId="report-reason-label"
                            value={selectedReason.toString()}
                            label={t("report.reportDialog.reasonLabel")}
                            onChange={handleReasonChange}
                        >
                            {reasons.map((reason) => (
                        <MenuItem key={reason.id} value={reason.id}>
                            {reason.title}
                        </MenuItem>
                            ))}
                        </Select>
                        <FormHelperText>{errorMsg}</FormHelperText>
                    </FormControl>

                    <TextField
                        fullWidth
                        multiline
                        rows={4}
                        label={t("report.reportDialog.descriptionLabel")}
                        placeholder={t("report.reportDialog.descriptionPlaceholder")}
                        value={reportContent}
                        onChange={(e) => {
                            setReportContent(e.target.value);
                            setError(false);
                        }}
                        error={error}
                        helperText={error ? errorMsg : ""}
                        variant="outlined"
                    />
                </Box>
            </DialogContent>
            <DialogActions sx={{ px: 3, pb: 2 }}>
                <Button onClick={handleClose} color="inherit">
                    {t("report.reportDialog.cancel")}
                </Button>
                <Button
                    onClick={handleSubmit}
                    variant="contained"
                    disabled={loading}
                    sx={{
                        background: "linear-gradient(135deg, #f44336 0%, #e91e63 100%)",
                        "&:hover": {
                            background: "linear-gradient(135deg, #e91e63 0%, #f44336 100%)",
                        },
                    }}
                >
                    {t("report.reportDialog.submit")}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

