/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.afrunt.jach.test;

import com.afrunt.jach.ACH;
import com.afrunt.jach.document.ACHDocument;
import com.afrunt.jach.domain.GeneralBatchHeader;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

/**
 * @author Andrii Frunt
 */
public class ACHTest {
    private static final String[] ACH_FILES = {"ach-ppd-contested-dishonored-return.txt", "ach-ppd-dishonored-return.txt", 
            "ach-ppd-return.txt","ach-cor2.txt", "ach-return-cor.txt","cie-returns.txt", "ach-return.txt", "ach-tr.txt", 
            "ach-payrol.txt", "ach-web-ppd.txt", "ach-pos.txt"
    };

    @Test
    public void testReadWrite() {
        long start = System.currentTimeMillis();
        ACH ach = new ACH();
        System.out.println(String.format("\nACH instantiated in %sms", (System.currentTimeMillis() - start)));
        for (String achFileName : ACH_FILES) {
            start = System.currentTimeMillis();
            ACHDocument document = ach.read(getClass().getClassLoader().getResourceAsStream(achFileName));
            System.out.println(String.format(achFileName + " read in %sms", (System.currentTimeMillis() - start)));

            start = System.currentTimeMillis();
            String out = ach.write(document);
            System.out.println(String.format(achFileName + " written in %sms", (System.currentTimeMillis() - start)));

            testFilesAreEquals(getClass().getClassLoader().getResourceAsStream(achFileName), new ByteArrayInputStream(out.getBytes()));
        }
    }

    @Test
    public void testBlockAligning() {
        ACH ach = new ACH()
                .withBlockAligning(true);

        ACHDocument document = ach.read(getClass().getClassLoader().getResourceAsStream("ach-payrol.txt"));
        String out = ach.write(document);
        String[] strings = out.split(ACH.LINE_SEPARATOR);
        Assert.assertEquals(10, strings.length);

        document = ach.read(getClass().getClassLoader().getResourceAsStream("ach-pos.txt"));
        out = ach.write(document);
        strings = out.split(ACH.LINE_SEPARATOR);
        Assert.assertEquals(10, strings.length);
    }

    @Test
    public void castTest(){
        GeneralBatchHeader batchHeader = new GeneralBatchHeader()
                .setCompanyID("")
                .setServiceClassCode("XXX")
                .cast(GeneralBatchHeader.class)
                .setCompanyName("")
                .setBatchNumber(1)
                .cast();
    }

    private void testFilesAreEquals(InputStream is1, InputStream is2) {
        Scanner sc1 = new Scanner(is1, ACH.DEFAULT_CHARSET.name());
        Scanner sc2 = new Scanner(is2, ACH.DEFAULT_CHARSET.name());

        while (sc1.hasNextLine()) {
            String line1 = sc1.nextLine();
            if (line1.trim().equals("")) {
                continue;
            }
            String line2 = sc2.nextLine();
            Assert.assertEquals(line1, line2);
        }
    }


}
