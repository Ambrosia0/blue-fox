import { apiClient } from "@services/apiClient";

export type ReportReason = {
    id: number,
    title: string,
    code: string,
    lang: string
}

export type TargetType = "post" | "user" | "comment"

export type ReportRequest = {
    reportReasonId: number,
    targetType: TargetType,
    reportContent: string,
    reportContentKey: string
}

export async function getReportReasons() {
    return (await apiClient.get<ReportReason[]>("/api/user/report")).data;
}

export async function sendReport(request: ReportRequest) {
    return (await apiClient.post("/api/user/report", request)).data;
}