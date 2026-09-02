import { Box, Container, Typography } from "@mui/material"
import { useRouteError, isRouteErrorResponse, ErrorResponse } from "react-router";
import { ThemeProvider } from "../context/ThemeContext";

export const ErrorDisplay = ({ status, statusText }: { status?: number, statusText?: string }) => {
	let error = useRouteError();
	const isRouteError = isRouteErrorResponse(error);
	if (!isRouteError && (!status && !statusText)) {
		return null;
	}

	let parsedError;

	if (isRouteError) {
		parsedError = parseRouteError(error);
	}


	// @ts-expect-error
	const isServerError = (status ?? error.status) >= 500;
	const imageSrc = isServerError ? "/confused_cirno" : "/dangerous_cirno";
	const imageAlt = isServerError ? "Server error" : "Client error";

	return (
		<ThemeProvider>
			<Container maxWidth="md">
				<Box
					display="flex"
					flexDirection="column"
					alignItems="center"
					justifyContent="center"
					textAlign="center"
					minHeight="60vh"
					gap={3}
				>

					<Typography variant="h2" fontWeight={700}>
						{status ?? (error as any).status} {statusText ?? (parsedError && parsedError["title"] ? parsedError["title"] : (error as any).statusText)}
					</Typography>
					{parsedError ?
						<Typography variant="body1" color="text.secondary">
							{parsedError && parsedError["title"] ? `${parsedError["title"]}. ${parsedError["detail"]}` : ""}
						</Typography> :
						<></>
					}
					<Box
						component="img"
						src={`${imageSrc}-512x512.webp`}
						srcSet={`
                  ${imageSrc}-256x256.webp 256w,
                  ${imageSrc}-256x256.webp 512w
                `}
						sizes="(max-width: 600px) 256px, 512px"
						alt={imageAlt}
						sx={{
							maxWidth: 300,
							width: "100%",
							height: "auto",
						}}
					/>
				</Box>
			</Container>
		</ThemeProvider>
	);
}

function parseRouteError(error: ErrorResponse) {
	let parsedError;
	try {
		parsedError = JSON.parse(error.data);
	} catch {
		parsedError = undefined;
	}
	return parsedError;
}