import { apiClient } from "@services/apiClient";
import type { Pageable, Slice } from "@services/types";

type TargetType = "POST" | "USER" | "COMMENT" | "COMMUNITY";

type Status = "OPEN" | "CLOSE";

export type ReportFilter = {
    status?: Status;
    targetType?: TargetType;
    direction?: "ASC" | "DESC";
}

export type Report = {
    id: string;
    userId: string;
    username: string;
    isEnabled: boolean;
    avatarId: string;
    reportReasonId: number;
    reportContent: string;
    targetType: TargetType;
    status: Status;
    reportedContentKey: string;
    resolvedBy?: string;
    createdAt: number
};

export type ReportTranslationCreate = {
    reasonId: number;
    title: string;
    lang: string; // iso639-1 code
}

type ReportReasonTranslation = {
    reasonId: number;
    lang: string;
    title: string;
}

type ReportReasonI18n = {
    reasonId: number;
    lang: string;
    title: string;
}

export type ReportReason = {
    id: number;
    code: string;
    i18n: ReportReasonI18n[];
}

export async function getReports(pageable?: Pageable, reportFilter?: ReportFilter) {
    return (await apiClient.get<Slice<Report>>("/api/admin/report", {
        params: {
            page: pageable?.page,
            size: pageable?.size,
            status: reportFilter?.status,
            targetType: reportFilter?.targetType,
            direction: reportFilter?.direction
        }
    })).data;
}

export async function closeRequest(id: string) {
    return (await apiClient.post(`/api/admin/report/${id}/status`));
}

export async function createReason(reasonId: number, lang: string) {
    return (await apiClient.post<ReportReasonTranslation>(`/api/admin/report/reason/${reasonId}/translation/${lang}`))
        .data;
}

export async function deleteTranslation(reasonId: number, lang: string) {
    return (await apiClient.delete(`/api/admin/report/reason/${reasonId}/translation/${lang}`));    
}

export async function getTranslations(reasonId: number) {
    return (await apiClient.get<ReportReasonTranslation[]>(`/api/admin/report/reason/${reasonId}/translation`)).data
}

export async function getReasons() {
    return (await apiClient.get<ReportReason[]>(`/api/admin/report/reason`)).data
}