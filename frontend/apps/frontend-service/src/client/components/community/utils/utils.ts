import { Scope, SCOPES } from "@services/user/userCommunityApi";
import { UserInfo } from "../../../types/user";

export function isInfoLoaded(user: {id: string} | UserInfo): user is UserInfo{
    return "username" in user;
}

export function missingScopes(scopes: Scope[]): Scope[]{
    return SCOPES.filter(scope => !scopes.includes(scope));
}