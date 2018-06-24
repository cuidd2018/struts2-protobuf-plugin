package com.github.junahan.struts2.test.bean;

import java.util.Collection;
import java.util.Objects;

public class SampleRepeatedSubMessageBean {
    private Collection<SubMessageBean> subMessages;

    public Collection<SubMessageBean> getSubMessages() {
        return subMessages;
    }

    public void setSubMessages(Collection<SubMessageBean> subMessages) {
        this.subMessages = subMessages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampleRepeatedSubMessageBean that = (SampleRepeatedSubMessageBean) o;
        return Objects.equals(getSubMessages(), that.getSubMessages());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getSubMessages());
    }

    @Override
    public String toString() {
        return "SampleRepeatedSubMessageBean{" +
                "subMessages=" + subMessages +
                '}';
    }
}
