package com.xuegao.LoginServer.vo;

import java.util.List;

public class X7DataVo {
        private String guid;
        private List<GuidData> guid_data;

        public String getGuid() {
            return guid;
        }
        public void setGuid(String guid) {
            this.guid = guid;
        }
        public List getGuid_data() {
            return guid_data;
        }
        public void setGuid_data(List guid_data) {
            this.guid_data = guid_data;
        }

        public static class GuidData{
            private String area = "";
            private String username = "";
            private String level = "";
            private String ce = "";
            private String stage = "";
            private AddData add_data =new AddData();

            public String getArea() {
                return area;
            }
            public void setArea(String area) {
                this.area = area;
            }
            public String getUsername() {
                return username;
            }
            public void setUsername(String username) {
                this.username = username;
            }
            public String getLevel() {
                return level;
            }
            public void setLevel(String level) {
                this.level = level;
            }
            public String getCe() {
                return ce;
            }
            public void setCe(String ce) {
                this.ce = ce;
            }
            public String getStage() {
                return stage;
            }
            public void setStage(String stage) {
                this.stage = stage;
            }
            public AddData getAdd_data() {
                return add_data;
            }
            public void setAdd_data(AddData add_data) {
                this.add_data = add_data;
            }

            public class AddData{
                private String part1 = "";

                public String getPart1() {
                    return part1;
                }
                public void setPart1(String part1) {
                    this.part1 = part1;
                }
            }
        }

}
