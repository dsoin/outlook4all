# outlook4all
Make your outlook archives, PDLs, emails searchable and accessible online
Simple restlet-based webapp with ElasticSearch in a backend. This is 2nd part of a solution to make your PDLs accessible and searchable
outside of Outlook. It expects an ES instance to have data already imported and just allows browsing and searching. 
It expects "emails" index to be present and the following mapping:
<TBD>

Use [EWS connector](https://github.com/dsoin/EWSPuller) to pull emails into ES index. 

<h1>License</h1>
MIT License

Copyright (c) 2016 Dmitrii Soin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
