import { useState } from "react";
import { Button, Tooltip } from "@mui/material";
import FlagIcon from "@mui/icons-material/Flag";
import { ReportDialog } from "./ReportDialog";
import { TargetType } from "@services/user/userReportApi";
import { useTranslation } from "react-i18next";

interface ReportButtonProps {
    targetType: TargetType;
    targetId: string;
    icon?: boolean;
    textColor?: string;
}

export const ReportButton = ({ targetType, targetId, icon = false, textColor }: ReportButtonProps) => {
    const [reportOpen, setReportOpen] = useState(false);
    const { t } = useTranslation();

    const handleSuccess = () => {
        setReportOpen(false);
    };

    const reportLabel = t("report.report");

    return (
        <>
            {icon ? (
                <Tooltip title={reportLabel}>
                    <Button
                        size="small"
                        onClick={() => setReportOpen(true)}
                        sx={{
                            color: textColor || "inherit",
                            padding: 0,
                            minWidth: "auto",
                            "&:hover": {
                                backgroundColor: "transparent",
                                color: "#f44336",
                            },
                        }}
                    >
                        <FlagIcon fontSize="inherit" sx={{ fontSize: 18 }} />
                    </Button>
                </Tooltip>
            ) : (
                <Tooltip title={reportLabel}>
                    <Button
                        size="small"
                        startIcon={<FlagIcon />}
                        onClick={() => setReportOpen(true)}
                        sx={{
                            color: textColor || "inherit",
                            padding: "4px 8px",
                            fontSize: "0.75rem",
                            backgroundColor: "transparent",
                            "&:hover": {
                                backgroundColor: "rgba(244, 67, 54, 0.08)",
                                color: "#f44336",
                            },
                        }}
                    >
                        {reportLabel}
                    </Button>
                </Tooltip>
            )}

            <ReportDialog
                open={reportOpen}
                onClose={() => setReportOpen(false)}
                onSuccess={handleSuccess}
                targetType={targetType}
                targetId={targetId}
            />
        </>
    );
};