import Image, { ImageOptions } from "@tiptap/extension-image";
import "./styles.scss"

export const ImageAlignmentExtension = Image.extend({

  addAttributes() {
    return {
      ...this.parent?.(),

      align: {
        default: "center",

        parseHTML: element =>
          element.getAttribute("data-align") || "center",

        renderHTML: attributes => {
          return attributes.align && attributes.align !== "center"
            ? { "data-align": attributes.align }
            : { "data-align": "center" };
        }
      }
    }
  }
})