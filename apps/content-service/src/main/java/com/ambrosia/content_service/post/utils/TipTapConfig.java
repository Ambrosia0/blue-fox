package com.ambrosia.content_service.post.utils;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

import com.ambrosia.content_service.post.utils.tiptap.TipTapDoc;

@RegisterReflectionForBinding({TipTapDoc.class})
@Configuration
public class TipTapConfig {}
