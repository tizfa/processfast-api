/*
 *
 * ****************
 * Copyright 2015 Tiziano Fagni (tiziano.fagni@isti.cnr.it)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ******************
 */

package it.cnr.isti.hlt.processfast.data;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.*;

/**
 * @author Tiziano Fagni (tiziano.fagni@isti.cnr.it)
 */
public class RecursiveIndexFileLineIteratorProvider implements ImmutableDataSourceIteratorProvider<RecursiveIndexFileLineIteratorProvider.LineInfo> {

    public static class LineInfo implements Serializable {
        public String filename;
        public int lineIndex;
        public String line;
    }

    private Collection<File> files;

    public RecursiveIndexFileLineIteratorProvider(String baseDir, String regexInclusion) {
        if (baseDir == null)
            throw new NullPointerException("The collection is 'null'");

        files = listFiles(baseDir, regexInclusion);
    }

    private Collection<File> listFiles(String baseDir, String regexInclusion) {

        Collection<File> files = null;
        if (regexInclusion == null || regexInclusion.isEmpty()) {
            files = FileUtils.listFiles(new File(baseDir),
                    TrueFileFilter.INSTANCE,
                    DirectoryFileFilter.DIRECTORY
            );
        } else {
            files = FileUtils.listFiles(new File(baseDir),
                    new RegexFileFilter(regexInclusion),
                    DirectoryFileFilter.DIRECTORY
            );
        }
        return files;
    }

    public Iterator<LineInfo> iterator() {
        return new FileLineIterator(files);
    }

    @Override
    public boolean sizeEnabled() {
        return true;
    }

    @Override
    public long size() {
        Iterator<LineInfo> it = iterator();
        long count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count;
    }

    @Override
    public boolean contains(LineInfo item) {
        return false;
    }

    @Override
    public boolean containsEnabled() {
        return false;
    }

    @Override
    public Collection<LineInfo> take(long startFrom, long numItems) {
        return null;
    }

    @Override
    public boolean takeEnabled() {
        return false;
    }


    private static class FileLineIterator implements Iterator<LineInfo> {

        private final Collection<File> files;
        private final Iterator<File> iter;
        private List<String> curFile;
        private int curIndexInFile;
        private String currentFilename;

        public FileLineIterator(Collection<File> files) {
            this.files = files;
            this.iter = files.iterator();
            if (iter.hasNext()) {
                File nextFile = iter.next();
                currentFilename = nextFile.getAbsolutePath();
                curFile = readFile(nextFile);
            } else {
                curFile = new ArrayList<>();
                currentFilename = "";
            }
            this.curIndexInFile = 0;
        }

        public boolean hasNext() {
            return (iter.hasNext() || curIndexInFile < curFile.size());
        }

        public LineInfo next() {
            if (!hasNext())
                throw new NoSuchElementException("There are no more lines to fetch");

            String line = curFile.get(curIndexInFile);
            LineInfo li = new LineInfo();
            li.filename = currentFilename;
            li.line = line;
            li.lineIndex = curIndexInFile;

            curIndexInFile++;

            if (curIndexInFile >= curFile.size()) {
                if (iter.hasNext()) {
                    File nextFile = iter.next();
                    currentFilename = nextFile.getAbsolutePath();
                    curFile = readFile(nextFile);
                    curIndexInFile = 0;
                }
            }

            return li;
        }

        public void remove() {
            throw new UnsupportedOperationException("The operation is not supported");
        }

        private ArrayList<String> readFile(File f) {
            ArrayList ret = new ArrayList();
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String l;
                while ((l = br.readLine()) != null) {
                    ret.add(l);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println("Letto " + f.toString());
            return ret;
        }
    }
}
