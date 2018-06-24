package com.github.junahan.struts2.test;

import com.github.junahan.struts2.ProtobufPopulator;
import com.github.junahan.struts2.test.bean.*;
import com.github.junahan.struts2.test.protocol.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class Bean2MessageTest extends BasicTestCase {

    @Test(expected = IllegalArgumentException.class)
    public void testNullBean() throws Exception {
        EnumAliasTestMessage.Builder expected = EnumAliasTestMessage.newBuilder();
        ProtobufPopulator populator = new ProtobufPopulator();
        populator.populateToMessage(expected, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullMessageBuilder() throws Exception {
        ProtobufPopulator populator = new ProtobufPopulator();
        populator.populateToMessage(null, new EnumAliasBean(), null, null);
    }

    @Test
    public void testEnumField() throws Exception {
        ProtobufPopulator populator = new ProtobufPopulator();
        EnumAliasBean testBean = new EnumAliasBean();
        testBean.setaField(1024);
        testBean.setaAllowingAliasEnum(EnumAllowingAlias.RUNNING);

        EnumAliasTestMessage.Builder expected = EnumAliasTestMessage.newBuilder();
        expected.setAField(testBean.getaField());
        expected.setAAllowingAliasEnum(testBean.getaAllowingAliasEnum());

        EnumAliasTestMessage.Builder actual = EnumAliasTestMessage.newBuilder();
        populator.populateToMessage(actual, testBean, null, null);

        assertEquals(expected.build(), actual.build());
    }

    @Test
    public void testRepeatedEnumField() throws Exception {
        RepeatedFieldBean testBean = new RepeatedFieldBean();
        testBean.addAllIds(generateIds(10));
        testBean.addAllTestMessage(generateTestBeans(10));
        RepeatedField.Builder expected = copyFrom(testBean);

        ProtobufPopulator populator = new ProtobufPopulator();
        RepeatedField.Builder actual = RepeatedField.newBuilder();
        populator.populateToMessage(actual, testBean, null, null);
        assertEquals(expected.build(), actual.build());
    }

    @Test
    public void testArrayField() throws Exception {
        RepeatedFieldBean2 rfb2 = new RepeatedFieldBean2();
        Integer[] ids = new Integer[10];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = random.nextInt();
        }
        rfb2.setIds(ids);

        RepeatedField.Builder expected = RepeatedField.newBuilder();
        for (Integer element:ids) {
            expected.addIds(element);
        }

        RepeatedField.Builder actual = RepeatedField.newBuilder();
        ProtobufPopulator populator = new ProtobufPopulator();
        populator.populateToMessage(actual, rfb2, null, null);
        assertEquals(actual.build(), expected.build());
    }

    @Test
    public void testNestedType() throws Exception {
        OuterBean ob = new OuterBean();
        ob.setIval(2048);
        ob.setBooly(false);

        // generate the message by the testBean.
        Outer.MiddleAA.Inner.Builder expected = Outer.MiddleAA.Inner.newBuilder();
        expected.setIval(ob.getIval());
        expected.setBooly(ob.isBooly());

        ProtobufPopulator populator = new ProtobufPopulator();
        Outer.MiddleAA.Inner.Builder actual1 = Outer.MiddleAA.Inner.newBuilder();
        populator.populateToMessage(actual1, ob, null, null);
        Outer.MiddleAA.Inner inner1 = actual1.build();
        assertEquals(inner1.getBooly(), false);
        assertEquals(inner1.getIval(), 2048);

        Outer.MiddleBB.Inner.Builder actual2 = Outer.MiddleBB.Inner.newBuilder();
        populator.populateToMessage(actual2, ob, null, null);
        Outer.MiddleBB.Inner inner2 = actual2.build();
        assertEquals(inner2.getBooly(), false);
        assertEquals(inner2.getIval(), 2048);
    }

    @Test
    public void testExtensionMessage() throws Exception {
        FooBean fb = new FooBean();
        fb.setName("Wold Cup");
        fb.setBar(32);
        fb.setMisc(64);

        Foo.Builder fooBuilder = Foo.newBuilder();
        ProtobufPopulator populator = new ProtobufPopulator();
        populator.populateToMessage(fooBuilder, fb, null, null);
        Foo foo = fooBuilder.build();
        assertEquals(foo.getName(), fb.getName());
        assertEquals(foo.getExtension(ProtocolTest.bar).intValue(), fb.getBar());
        assertEquals(foo.getExtension(ProtocolTest.misc).longValue(), fb.getMisc());
    }

    @Test
    public void testExtensionNestedMessage() throws Exception {
        FooBean fb = new FooBean();
        BazBean baz = new BazBean();
        baz.setMisc(2^10);
        fb.setName(UUID.randomUUID().toString());
        fb.setBar(random.nextInt());
        fb.setMisc(random.nextInt());
        fb.setFooExt(baz);

        Foo.Builder fooBuilder = Foo.newBuilder();
        ProtobufPopulator populator = new ProtobufPopulator();
        populator.populateToMessage(fooBuilder, fb, null, null);
        Foo foo = fooBuilder.build();
        assertEquals(foo.getName(), fb.getName());
        assertEquals(foo.getExtension(ProtocolTest.bar).intValue(), fb.getBar());
        assertEquals(foo.getExtension(ProtocolTest.misc).longValue(), fb.getMisc());
        Baz2 b= foo.getExtension(Baz2.fooExt);
        assertEquals(b.getMisc(), baz.getMisc());
    }

    @Test
    public void testMessageField() throws Exception {
        SubMessageBean sub = new SubMessageBean();
        sub.setSubName("sub");
        SampleMessageBean bean = new SampleMessageBean();
        bean.setSubMessage(sub);

        SubMessage.Builder subBuilder = SubMessage.newBuilder();
        subBuilder.setSubName(sub.getSubName());
        SampleMessage.Builder expected = SampleMessage.newBuilder();
        expected.setSubMessage(subBuilder.build());

        SampleMessage.Builder actual = SampleMessage.newBuilder();
        ProtobufPopulator populator = new ProtobufPopulator();
        populator.populateToMessage(actual, bean, null, null);
        assertEquals(actual.build(), expected.build());
    }

    @Test
    public void testRepeatedMessageField() throws Exception {
        SubMessageBean sub1 = new SubMessageBean();
        sub1.setSubName("sub1");
        SubMessageBean sub2 = new SubMessageBean();
        sub2.setSubName("sub2");
        SampleRepeatedSubMessageBean sampleBean = new SampleRepeatedSubMessageBean();
        List<SubMessageBean> subs = new ArrayList<SubMessageBean>();
        subs.add(sub1);
        subs.add(sub2);
        sampleBean.setSubMessages(subs);

        SampleRepeatedMessage.Builder actual = SampleRepeatedMessage.newBuilder();
        ProtobufPopulator populator = new ProtobufPopulator();
        populator.populateToMessage(actual, sampleBean, null, null);
        Collection<SubMessage> subBeans = actual.build().getSubMessagesList();
        assertNotNull(subBeans);
        assertEquals(2, subBeans.size());
        assertEquals(sub1.getSubName(), ((List<SubMessage>) subBeans).get(0).getSubName());
        assertEquals(sub2.getSubName(), ((List<SubMessage>) subBeans).get(1).getSubName());
    }

    @Test
    public void testUnreadableField() throws Exception {
        ProtobufPopulator populator = new ProtobufPopulator();
        EnumAliasBean2 testBean = new EnumAliasBean2();
        testBean.setaField(1024);
        testBean.setaAllowingAliasEnum(EnumAllowingAlias.RUNNING);

        EnumAliasTestMessage.Builder actual = EnumAliasTestMessage.newBuilder();
        populator.populateToMessage(actual, testBean, null, null);

        EnumAliasTestMessage m = actual.build();
        assertNotNull(m);
        assertEquals(m.getAAllowingAliasEnum(), testBean.getaAllowingAliasEnum());
        assertTrue(m.getAField() != 1024);
    }

    @Test
    public void testNonExistField() throws Exception {
        ProtobufPopulator populator = new ProtobufPopulator();
        EnumAliasBean3 testBean = new EnumAliasBean3();
        testBean.setaAllowingAliasEnum(EnumAllowingAlias.RUNNING);

        EnumAliasTestMessage.Builder actual = EnumAliasTestMessage.newBuilder();
        populator.populateToMessage(actual, testBean, null, null);

        EnumAliasTestMessage m = actual.build();
        assertNotNull(m);
        assertEquals(m.getAAllowingAliasEnum(), testBean.getaAllowingAliasEnum());
        assertEquals(m.getAField(), 0);
    }

    @Test
    public void testNullField() throws Exception {
        ProtobufPopulator populator = new ProtobufPopulator();
        EnumAliasBean testBean = new EnumAliasBean();
        testBean.setaAllowingAliasEnum(null);

        EnumAliasTestMessage.Builder actual = EnumAliasTestMessage.newBuilder();
        actual.setAAllowingAliasEnum(EnumAllowingAlias.RUNNING);
        populator.populateToMessage(actual, testBean, null, null);

        EnumAliasTestMessage m = actual.build();
        assertNotNull(m);
        assertEquals(m.getAAllowingAliasEnum(), EnumAllowingAlias.RUNNING);
    }

    @Test
    public void testExclusion() throws Exception {
        ProtobufPopulator populator = new ProtobufPopulator();
        EnumAliasBean testBean = new EnumAliasBean();
        testBean.setaField(1024);
        testBean.setaAllowingAliasEnum(EnumAllowingAlias.RUNNING);

        EnumAliasTestMessage.Builder actual = EnumAliasTestMessage.newBuilder();
        List<String> exclusion = new ArrayList<>();
        exclusion.add("aField");
        populator.populateToMessage(actual, testBean, null, exclusion);

        EnumAliasTestMessage m = actual.build();
        assertNotNull(m);
        assertEquals(m.getAAllowingAliasEnum(), testBean.getaAllowingAliasEnum());
        assertTrue(m.getAField() != 1024);
    }

    private RepeatedField.Builder copyFrom(RepeatedFieldBean bean) {
        RepeatedField.Builder rfb = RepeatedField.newBuilder();
        if (bean != null) {
            Collection<Integer> ids = bean.getIds();
            rfb.addAllIds(ids);
            Collection<EnumAliasBean> messages = bean.getTestMessages();
            for (EnumAliasBean message:messages) {
                rfb.addTestMessages(copyFrom(message).build());
            }
        }
        return rfb;
    }

    private EnumAliasTestMessage.Builder copyFrom(EnumAliasBean bean) {
        EnumAliasTestMessage.Builder etmb = EnumAliasTestMessage.newBuilder();
        if (bean != null) {
            etmb.setAField(bean.getaField());
            etmb.setAAllowingAliasEnum(bean.getaAllowingAliasEnum());
        }
        return etmb;
    }

    private Collection<Integer> generateIds(int number) {
        Collection<Integer> ids = new ArrayList<Integer>();
        for (int i = 0; i < number; i++) {
            ids.add(i);
        }
        return ids;
    }

    private Collection<EnumAliasBean> generateTestBeans(int number) {
        Collection<EnumAliasBean> testBeans = new ArrayList<EnumAliasBean>();
        for (int i = 0; i < number; i++) {
            EnumAliasBean testBean = new EnumAliasBean();
            testBean.setaField(random.nextInt());
            testBean.setaAllowingAliasEnum(EnumAllowingAlias.forNumber(random.nextInt(2)));
            testBeans.add(testBean);
        }
        return testBeans;
    }
}
