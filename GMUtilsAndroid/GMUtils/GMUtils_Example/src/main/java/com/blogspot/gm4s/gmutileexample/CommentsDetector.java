package com.blogspot.gm4s.gmutileexample;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class CommentsDetector {

    public static class Comments {

        public enum CommentTypes {
            /**
             * starts with double slash (//) and ends with new line character (\n)
             * example:
             * //this simple comment that consist of only one single line
             */
            Simple,
            /**
             * starts with slash and asterisks and ends with asterisks and slash
             * example:
             * /* this is composite comment
             * that can consist of multiple lines * / (remove space between * and /)
             */
            Composite
        }

        public static class Comment {
            private CommentTypes type;
            private String text;
            private int position;

            public Comment setType(CommentTypes type) {
                this.type = type;
                return this;
            }

            public Comment setText(String text) {
                this.text = text;
                return this;
            }

            public Comment setPosition(int position) {
                this.position = position;
                return this;
            }

            @Override
            public String toString() {
                return "Comment{" +
                        "\n- type= " + type.name() +
                        "\n- text= '" + text + '\'' +
                        "\n- position= " + position +
                        "\n- length= " + text.length() +
                        "\n}";
            }
        }

        private final List<Comment> comments = new ArrayList<>();

        public void addComment(Comment comment) {
            comments.add(comment);
        }

        @Override
        public String toString() {
            String string = "";

            for (Comment comment : comments) {
                string += comment.toString() + "\n";
            }

            return string;
        }
    }


    public static void main(String[] args) {
        runTesting();
    }

    public static void runTesting() {
        CommentsDetector detector = new CommentsDetector();

        String text1;
        Comments comments1 = detector.detectComments(text1 = "//this is simple comment 1" +
                " and //this is not simple comment");

        String text2;
        Comments comments2 = detector.detectComments(text2 = "//this is simple comment 1\n" +
                "and //this is simple comment 2");

        String text3;
        Comments comments3 = detector.detectComments(text3 = "//this is simple comment 1\n" +
                "and /*this is composite comment 1 in single line*/");

        String text4;
        Comments comments4 = detector.detectComments(text4 = "following //simple comment 1\n" +
                "then /*a composite comment with\n" +
                "multiple lines*/");

        String text5;
        Comments comments5 = detector.detectComments(text5 = "following /*a composite comment with no end\n" +
                "will embed //this simple comment inside it");

        String[] texts = new String[] {text1, text2, text3, text4, text5};
        Comments[] comments = new Comments[] {comments1, comments2, comments3, comments4, comments5};

        for (int i = 0; i < texts.length; i++) {
            System.out.println((i + 1) + "> ----------------------");
            System.out.println(texts[i]);
            System.out.println();
            System.out.println(comments[i]);
        }
    }

    public Comments detectComments(String text) {
        Comments comments = new Comments();

        if (text != null && !text.equals("")) {
            int lastPosition = 0;
            do {
                int simplePosition = text.indexOf("//", lastPosition);
                int compositePosition = text.indexOf("/*", lastPosition);

                int startPosition;
                int endPosition;
                Comments.CommentTypes type;

                if (simplePosition >= 0 && compositePosition < 0) {
                    startPosition = simplePosition + 2;
                    endPosition = text.indexOf("\n", startPosition);
                    type = Comments.CommentTypes.Simple;

                } else if (compositePosition >= 0 && simplePosition < 0) {
                    startPosition = compositePosition + 2;
                    endPosition = text.indexOf("*/", startPosition);
                    type = Comments.CommentTypes.Composite;

                } else if (simplePosition < compositePosition) {
                    startPosition = simplePosition + 2;
                    endPosition = text.indexOf("\n", startPosition);
                    type = Comments.CommentTypes.Simple;

                } else if (simplePosition > compositePosition) {
                    startPosition = compositePosition + 2;
                    endPosition = text.indexOf("*/", startPosition);
                    type = Comments.CommentTypes.Composite;

                } else {
                    break;
                }

                String commentText;
                if (startPosition >= 0 && endPosition > 0 && startPosition < endPosition) {
                    commentText = text.substring(startPosition, endPosition);

                } else if (startPosition >= 0) {
                    commentText = text.substring(startPosition);

                } else {
                    break;
                }

                comments.addComment(
                        new Comments.Comment()
                                .setType(type)
                                .setText(commentText)
                                .setPosition(startPosition)
                );

                if (endPosition < 0) break;

                lastPosition = endPosition;

            } while (true);
        }

        return comments;
    }
}
