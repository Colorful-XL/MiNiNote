package com.example.mininote.database;

public class NoteDbSchema {
    public static final class NoteTable{//定义描述数据表元素的String常量
        public static final String Name = "Notes";
        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String NOTIDATE = "notidate";
            public static final String SECONDLINE = "secondLine";
            public static final String CONTENT="content";
            public static final String SETNOTI="setnoti";
        }
    }
}
